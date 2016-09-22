/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jassoft.markets.utils;

import com.jassoft.markets.feed.Feed;
import com.jassoft.markets.feed.FeedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;

public class RSSFeedParser {

    private static final Logger LOG = LoggerFactory.getLogger(RSSFeedParser.class);

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    static final String CHANNEL = "channel";
    private static final String LANGUAGE = "language";
    private static final String COPYRIGHT = "copyright";
    private static final String LINK = "link";
    private static final String AUTHOR = "author";
    private static final String ITEM = "item";
    private static final String PUB_DATE = "pubDate";
    private static final String GUID = "guid";

    private final InputStream feedStream;

    public RSSFeedParser(InputStream feed) {
        this.feedStream = feed;
    }

    public Feed readFeed() {
        Feed feed = null;
        InputStream in = null;
        
        try {
            boolean isFeedHeader = true;
            // Set header values intial to the empty string
            String description = "";
            String title = "";
            String link = "";
            String language = "";
            String copyright = "";
            String author = "";
            String pubdate = "";
            String guid = "";

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            XMLEventReader eventReader = inputFactory.createXMLEventReader(feedStream);
            // read the XML document
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String localPart = event.asStartElement().getName()
                            .getLocalPart();
                    switch (localPart) {
                        case ITEM:
                            if (isFeedHeader) {
                                isFeedHeader = false;
                                feed = new Feed(title, link, description, language,
                                        copyright, pubdate);
                            }
                            break;
                        case TITLE:
                            title = getCharacterData(eventReader);
                            break;
                        case DESCRIPTION:
                            description = getCharacterData(eventReader);
                            break;
                        case LINK:
                            link = getCharacterData(eventReader);
                            break;
                        case GUID:
                            guid = getCharacterData(eventReader);
                            break;
                        case LANGUAGE:
                            language = getCharacterData(eventReader);
                            break;
                        case AUTHOR:
                            author = getCharacterData(eventReader);
                            break;
                        case PUB_DATE:
                            pubdate = getCharacterData(eventReader);
                            break;
                        case COPYRIGHT:
                            copyright = getCharacterData(eventReader);
                            break;
                    }
                }
                else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(ITEM)) {
                    FeedMessage message = new FeedMessage();
                    message.setAuthor(author);
                    message.setDescription(description);
                    message.setGuid(guid);
                    message.setLink(link);
                    message.setTitle(title);
                    feed.getMessages().add(message);
                }
            }
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        finally {
            if(in != null) {
                try {
                    in.close();
                }
                catch (IOException exception) {
                    LOG.error(exception.getLocalizedMessage(), exception);
                }
            }
        }
        return feed;
    }

    private String getCharacterData(XMLEventReader eventReader)
            throws XMLStreamException {
        String result = "";
        XMLEvent event = eventReader.nextEvent();
        if (event instanceof Characters) {
            result = event.asCharacters().getData();
        }
        return result;
    }
}
