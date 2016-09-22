package com.jassoft.markets.service;

import com.jassoft.markets.datamodel.crawler.Link;
import com.jassoft.markets.repository.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Created by jonshaw on 17/06/2016.
 */
@Service
public class LinkService {

    @Autowired
    private LinkRepository linkRepository;

    @Cacheable(value = "links", key = "#link")
    public Link findOneByLink(String link) {
        return linkRepository.findOneByLink(link);
    }

    @CacheEvict(value = "links", key = "#link.link")
    public void delete(Link link) {
        linkRepository.delete(link);
    }

    @CachePut(value = "links", key = "#link.link")
    public Link save(Link link) {
        return linkRepository.save(link);
    }
}
