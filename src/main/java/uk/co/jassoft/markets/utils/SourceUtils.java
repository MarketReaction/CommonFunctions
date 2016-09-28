/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.jassoft.markets.utils;

import uk.co.jassoft.markets.datamodel.sources.SourceUrl;

import java.util.List;

/**
 *
 * @author Jonny
 */
public class SourceUtils
{
    public static boolean matchesExclusion(List<String> exclusions, String link)
    {
        if(exclusions != null)
        {
            return exclusions.parallelStream()
                    .anyMatch(exclusion -> link.contains(exclusion));
        }
        
        return false;
    }
    
    public static boolean isValidURL(List<SourceUrl> allowedUrls, String url)
    {
        return allowedUrls.parallelStream()
                .anyMatch(sourceUrl -> url.startsWith(sourceUrl.getUrl()));
    }
}
