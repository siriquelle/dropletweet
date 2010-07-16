package com.dropletweet.test.oauth;

import com.dropletweet.dao.TweetDao;
import com.dropletweet.dao.UserDao;
import com.dropletweet.domain.Tweet;
import com.dropletweet.test.StartUp;
import com.dropletweet.util.DLog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.Test;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class OAuth {

    private TweetDao tweetDao;
    private UserDao userDao;

    public OAuth()
    {
        DLog.log("OAuth OBJECT CREATED");
    }

    public void Setup()
    {
        DLog.log("START OAuth SETUP");
        StartUp.startUp();
        tweetDao = (TweetDao) StartUp.ctx.getBean("tweetDao");
        userDao = (UserDao) StartUp.ctx.getBean("userDao");
        DLog.log("END OAuth SETUP");
    }

    @Test
    public void login()
    {
        this.Setup();
        try
        {
            // The factory instance is re-useable and thread safe.
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer("pESR4xRk4glcGKMCryvdMQ", "ddu3r7IRp0I1luSFOW2iLwKP5pYTY03AZrAjJAVaj8");

            RequestToken requestToken = twitter.getOAuthRequestToken();
            AccessToken accessToken = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while (null == accessToken)
            {
                System.out.println("Open the following URL and grant access to your account:");
                System.out.println(requestToken.getAuthorizationURL());
                System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
                String pin = null;
                try
                {
                    pin = br.readLine();
                } catch (IOException ex)
                {
                    DLog.log(ex.getMessage());
                }
                try
                {
                    if (pin.length() > 0)
                    {
                        accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                    } else
                    {
                        accessToken = twitter.getOAuthAccessToken();
                    }
                } catch (TwitterException te)
                {
                    if (401 == te.getStatusCode())
                    {
                        System.out.println("Unable to get the access token.");
                    } else
                    {
                        te.printStackTrace();
                    }
                }
            }
            //persist to the accessToken for future reference.
            AccessToken accessTokenTwo = this.storeAccessToken(twitter.verifyCredentials(), accessToken);
            Status status = twitter.updateStatus("@Siriquelle this is an example tweet. Feel free to contribute");
            System.out.println("Successfully updated the status to [" + status.getText() + "].");
            Tweet tweet = new Tweet(status);
            tweetDao.save(tweet);
            twitter.setOAuthAccessToken(accessTokenTwo);
            Status statusTwo = twitter.updateStatus("And Hay Presto.. It's working", status.getId());
            Tweet tweetTwo = new Tweet(statusTwo);
            tweetDao.save(tweetTwo);
            System.out.println("Successfully updated the status to [" + statusTwo.getText() + "].");

            System.exit(0);
        } catch (TwitterException ex)
        {
            DLog.log(ex.getMessage());
        }
    }

    private AccessToken storeAccessToken(User user, AccessToken accessToken)
    {
        com.dropletweet.domain.User dropletUser = new com.dropletweet.domain.User(user);
        dropletUser.setAccess_token(accessToken.getToken());
        dropletUser.setAccess_token_secret(accessToken.getTokenSecret());

        if (userDao.getByScreen_Name(user.getScreenName()) != null && userDao.getByScreen_Name(user.getScreenName()).getStatuses_count() == null)
        {
            userDao.delete(userDao.getByScreen_Name(user.getScreenName()));
        }
        userDao.save(dropletUser);
        System.out.println(user.getScreenName());

        return new AccessToken(dropletUser.getAccess_token(), dropletUser.getAccess_token_secret());
    }
}
