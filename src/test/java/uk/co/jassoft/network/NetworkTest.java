/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.jassoft.network;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
/**
 *
 * @author Jonny
 */
public class NetworkTest
{
    private static final Logger LOG = LoggerFactory.getLogger(NetworkTest.class);

    private final Network instance;
            
    public NetworkTest()
    {
        instance = new Network();
    }

    /**
     * Test of httpRequest method, of class Network.
     */
    @Test
    public void testHttpRequest() throws Exception
    {
        System.out.println("httpRequest");
        String httpUrl = "http://twitter.com";
        String method = "GET";

        String result = instance.httpRequest(httpUrl, method, false);

        LOG.info("Result [{}]", result);

        assertNotNull(result);

        assertFalse(result.isEmpty());
    }

    /**
     * Test of httpRequest method, of class Network.
     */
    @Test (expected = IOException.class)
    public void testHttpRequestwithMimeType_throwsIOException() throws Exception
    {
        System.out.println("httpRequest");
        String httpUrl = "http://www.mercyforanimals.org/files/Edge-Report.pptx";
        String method = "GET";

        String result = instance.httpRequest(httpUrl, method, false);

        LOG.info("Result [{}]", result);

        assertNotNull(result);

        assertFalse(result.isEmpty());
    }

    /**
     * Test of read method, of class Network.
     */
//    public void testRead() throws Exception
//    {
//        System.out.println("read");
//        String httpUrl = "http://www.newsnow.co.uk/A/724556456?-461:";
//        String method = "GET";
//
//        InputStream result = instance.read(httpUrl, method);
//
//        assertNotNull(result);
//    }
    
}
