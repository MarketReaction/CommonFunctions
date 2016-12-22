/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.jassoft.markets.utils.article;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import uk.co.jassoft.markets.exceptions.article.ArticleContentException;
import uk.co.jassoft.network.Network;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Jonny
 */
public class ContentGrabberTest
{
    private ContentGrabber contentGrabber;
    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);
        
        contentGrabber = new ContentGrabber();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    }

    /**
     * Test of getContentFromWebsite method, of class ContentGrabber.
     */
    @Test
    public void testGetContentFromWebsite() throws ArticleContentException, IOException {

        String html = IOUtils.toString(this.getClass().getResourceAsStream("/testWebPage.html"));
     
        String expResult = "Netflix boss hits out at ISP fees";
        String result = contentGrabber.getContentFromWebsite(html);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of getContentFromWebsite method, of class ContentGrabber.
     */
    @Test
    public void testGetContentFromGoogleFinance() throws IOException, ArticleContentException {
        Network network = new Network();
        
        String html = network.httpRequest("https://www.google.com/finance?q=NASDAQ%3AAAPL", "GET", false);
        
        String expResult = "Apple Inc. (Apple) designs, manufactures";
        String result = contentGrabber.getContentFromWebsite(html);
        assertTrue(result.contains(expResult));
    }

    /**
     * Test of getPublishedDate method, of class ContentGrabber.
     * @throws java.net.MalformedURLException
     */
    @Test
    public void testGetPublishedDateForYahoo() throws MalformedURLException
    {        
        String html = "<html><head><meta itemprop=\"datePublished\" content=\"2014-04-09T17:43:10Z\"/></head></html>";
        
        Date publishedDate = contentGrabber.getPublishedDate(html);
        
        assertEquals(dateFormat.format(new DateTime(2014, 4, 9, 17, 43, DateTimeZone.UTC).toDate()), dateFormat.format(publishedDate));
    }

    /**
     * Test of getPublishedDate method, of class ContentGrabber.
     * @throws java.net.MalformedURLException
     */
    @Test
    public void testGetPublishedDateForBBC() throws MalformedURLException
    {        
       String html = "<html><head><meta name=\"OriginalPublicationDate\" content=\"2014/04/21 14:51:52\"/></head></html>";
        
        Date publishedDate = contentGrabber.getPublishedDate(html);
        
        assertEquals(dateFormat.format(new DateTime(2014, 4, 21, 14, 51).toDate()), dateFormat.format(publishedDate));
    }

    public void testGetPublishedDate(String metaTag, Date shouldMatch)
    {
        String html = metaTag;
        
        Date publishedDate = contentGrabber.getPublishedDate(html);
        
        assertEquals(dateFormat.format(shouldMatch), dateFormat.format(publishedDate));
    }

    @Test
    public void testGetPublishedDate1()
    {
        String metaTag = "<time itemprop=\"datePublished\" content=\"2014-07-22\" datetime=\"1406043154\" pubdate>22 July 2014</time>";
        Date date = new DateTime(2014, 7, 22, 0, 0).toDate();

        testGetPublishedDate(metaTag, date);
    }

    @Test
    public void testGetPublishedDateInValue()
    {
        String metaTag = "<time>2014-07-22</time>";
        Date date = new DateTime(2014, 7, 22, 0, 0).toDate();

        testGetPublishedDate(metaTag, date);
    }

    @Test
    @Ignore("Currently doesn't work")
    public void testGetPublishedDate2()
    {
        String metaTag = "<time itemprop=\"dateCreated\" datetime=\"2014-07-22:16:53 BST\">Tuesday, Jul 22 2014, 16:53 BST</time>";
        Date date = new DateTime(2014, 7, 22, 16, 53).toDate();

        testGetPublishedDate(metaTag, date);
    }

    @Test
    public void testGetPublishedDate3()
    {
        String metaTag = "<time itemprop=\"datePublished\" datetime=\"2014-07-22T16:57+0100\">";
        Date date = new DateTime(2014, 7, 22, 16, 57, DateTimeZone.forOffsetHours(1)).toDate();

        testGetPublishedDate(metaTag, date);
    }

    @Test
    public void testGetPublishedDate4()
    {
        String metaTag = "<time pubdate=\"\" datetime=\"2014-07-22T16:57:00Z\">Published 22nd July 2014</time>";
        Date date = new DateTime(2014, 7, 22, 16, 57, DateTimeZone.UTC).toDate();

        testGetPublishedDate(metaTag, date);
    }

    @Test
    public void testGetPublishedDate5()
    {
        String metaTag = "<meta name=\"sailthru.date\" content=\"2016-03-17T12:44:53+0000\" />";
        Date date = new DateTime(2016, 3, 17, 12, 44, DateTimeZone.UTC).toDate();

        testGetPublishedDate(metaTag, date);
    }

    @Test
    public void testGetPublishedDate6()
    {
        String metaTag = "<meta name=\"sailthru.time\" content=\"2016-03-17T12:44:53+0000\" />";
        Date date = new DateTime(2016, 3, 17, 12, 44, DateTimeZone.UTC).toDate();

        testGetPublishedDate(metaTag, date);
    }

    @Test
    public void testGetPublishedDate7()
    {
        String metaTag = "<time class=\"timestamp\"> Jan. 25, 2016 5:07 p.m. ET </time>";
        Date date = new DateTime(2016, 1, 25, 17, 7, DateTimeZone.forOffsetHours(-5)).toDate();

        testGetPublishedDate(metaTag, date);
    }

    @Test
    public void testGetPublishedDate8()
    {
        String metaTag = "<time data-microtimes=\"{&quot;published&quot;:&quot;1457368507000&quot;,&quot;display&quot;:0,&quot;changed&quot;:&quot;1457368507000&quot;}\" datetime=\"16:35, 7 March 2016\">Monday 7 March 2016 16:35 BST</time>";
        Date date = new DateTime(2016, 3, 7, 16, 35, DateTimeZone.UTC).toDate();

        testGetPublishedDate(metaTag, date);
    }

    @Test
    public void testGetPublishedDate9()
    {
        String metaTag = "<time class=\"timestamp\"> Updated Jan. 29, 2015 2:48 p.m. ET </time>";
        Date date = new DateTime(2015, 1, 29, 14, 48, DateTimeZone.forOffsetHours(-5)).toDate();

        testGetPublishedDate(metaTag, date);
    }

    @Test
    public void testGetPublishedDate10()
    {
        String metaTag = "<time>Tuesday, 17 Jul 2012 | 10:00 AM ET</time>";
        Date date = new DateTime(2012, 7, 17, 10, 00, DateTimeZone.forOffsetHours(-5)).toDate();

        testGetPublishedDate(metaTag, date);
    }

}
