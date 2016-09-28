/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.jassoft.markets.utils.lingual;

import uk.co.jassoft.markets.datamodel.exclusion.Exclusion;
import uk.co.jassoft.markets.datamodel.story.NamedEntities;
import uk.co.jassoft.markets.datamodel.story.NamedEntity;
import uk.co.jassoft.markets.repository.ExclusionRepository;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Jonny
 */
public class NamedEntityRecognizer
{
    private static final Logger LOG = LoggerFactory.getLogger(NamedEntityRecognizer.class);

    private ExclusionRepository exclusionRepository;

    private final StanfordCoreNLP pipeline;

    public NamedEntityRecognizer(String propertiesFilePrefix)
    {
        this.pipeline = new StanfordCoreNLP(propertiesFilePrefix, false);
    }

    @Autowired
    public void setExclusionRepository(ExclusionRepository exclusionRepository) {
        this.exclusionRepository = exclusionRepository;
    }

    private StanfordCoreNLP getPipeline()
    {
        return pipeline;
    }

    public NamedEntities analyseStory(String content) {
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(content);

        // run all Annotators on this text
        getPipeline().annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        NamedEntities entities = new NamedEntities();
        
        for (CoreMap sentence : sentences)
        {
            int lastPersonPosition = 0;
            int lastMiscPosition = 0;
            int lastOrganisationPosition = 0;
            int lastLocationPosition = 0;
            
            String lastPersonWord = "";
            String lastMiscWord = "";
            String lastOrganisationWord = "";
            String lastLocationWord = "";
                
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            int wordPosition = 0;
            for (CoreLabel token : sentence.get(TokensAnnotation.class))
            {
                // increment the position in the test
                wordPosition++;
                // this is the text of the token
                String word = token.get(TextAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(NamedEntityTagAnnotation.class);

                switch(ne)
                {
                    case "PERSON":
                        if(lastPersonPosition > 0 && wordPosition == lastPersonPosition +1)
                            word = lastPersonWord + " " + word;
                        
                        lastPersonPosition = wordPosition;
                        lastPersonWord = word;


                        NamedEntities.addAndIncrementEntity(entities.getPeople(), word, sentence.toString());
                        
                        break;
                        
                    case "MISC":
                        if(lastMiscPosition > 0 && (wordPosition == lastMiscPosition+1))
                            word = lastMiscWord + " " + word;
                        
                        lastMiscPosition = wordPosition;
                        lastMiscWord = word;

                        NamedEntities.addAndIncrementEntity(entities.getMisc(), word, sentence.toString());
                        
                        break;
                        
                    case "ORGANIZATION":             
                        if(lastOrganisationPosition > 0 && wordPosition == lastOrganisationPosition+ 1)
                            word = lastOrganisationWord + " " + word;
                        
                        lastOrganisationPosition = wordPosition;
                        lastOrganisationWord = word;

                        NamedEntities.addAndIncrementEntity(entities.getOrganisations(), word, sentence.toString());
                        
                        break;
                        
                    case "LOCATION":
                        if(lastLocationPosition > 0 && wordPosition == lastLocationPosition+ 1)
                            word = lastLocationWord + " " + word;
                        
                        lastLocationPosition = wordPosition;
                        lastLocationWord = word;

                        NamedEntities.addAndIncrementEntity(entities.getLocations(), word, sentence.toString());
                        
                        break;                        
                }
            }
        }
        
        return entities;
    }

    public NamedEntities analyseCompany(String content) {

        // run all Annotators on this text
        Annotation document = getPipeline().process(content);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        NamedEntities entities = new NamedEntities();

        for (CoreMap sentence : sentences)
        {
            int lastPersonPosition = 0;
            int lastMiscPosition = 0;
            int lastOrganisationPosition = 0;
            int lastLocationPosition = 0;

            String lastPersonWord = "";
            String lastMiscWord = "";
            String lastOrganisationWord = "";
            String lastLocationWord = "";

            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            int wordPosition = 0;
            for (CoreLabel token : sentence.get(TokensAnnotation.class))
            {
                // increment the position in the test
                wordPosition++;
                // this is the text of the token
                String word = token.get(TextAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(NamedEntityTagAnnotation.class);

                switch(ne)
                {
                    case "PERSON":
                        if(lastPersonPosition > 0 && wordPosition == lastPersonPosition +1)
                        {
                            if(lastPersonPosition == wordPosition-1 && !lastPersonWord.contains(" "))
                            {
                                // Remove Single Word if part of longer name
                                removeName(entities.getPeople(), lastPersonWord);
                            }
                            word = lastPersonWord + " " + word;
                        }

                        lastPersonPosition = wordPosition;
                        lastPersonWord = word;

                        addOrIncrement(entities.getPeople(), word);

                        break;

                    case "MISC":
                        if(lastMiscPosition > 0 && wordPosition == lastMiscPosition +1)
                        {
                            if(lastMiscPosition == wordPosition-1 && !lastMiscWord.contains(" "))
                            {
                                // Remove Single Word if part of longer name
                                removeName(entities.getMisc(), lastMiscWord);
                            }
                            word = lastMiscWord + " " + word;
                        }

                        lastMiscPosition = wordPosition;
                        lastMiscWord = word;

                        addOrIncrement(entities.getMisc(), word);

                        break;

                    case "ORGANIZATION":
                        if(lastOrganisationPosition > 0 && wordPosition == lastOrganisationPosition +1)
                        {
                            if(lastOrganisationPosition == wordPosition-1 && !lastOrganisationWord.contains(" "))
                            {
                                // Remove Single Word if part of longer name
                                removeName(entities.getOrganisations(), lastOrganisationWord);
                            }
                            word = lastOrganisationWord + " " + word;
                        }

                        lastOrganisationPosition = wordPosition;
                        lastOrganisationWord = word;

                        addOrIncrement(entities.getOrganisations(), word);

                        break;

                    case "LOCATION":
                        if(lastLocationPosition > 0 && wordPosition == lastLocationPosition +1)
                        {
                            if (lastLocationPosition == wordPosition-1 && !lastLocationWord.contains(" "))
                            {
                                // Remove Single Word if part of longer name
                                removeName(entities.getLocations(), lastLocationWord);
                            }
                            word = lastLocationWord + " " + word;
                        }

                        lastLocationPosition = wordPosition;
                        lastLocationWord = word;

                        addOrIncrement(entities.getLocations(), word);

                        break;
                }
            }
        }

        return entities;
    }

    public void addOrIncrement(Collection<NamedEntity> collection, String name)
    {
        name = name.replaceAll("[^a-zA-Z0-9:\\s\\-]", "").trim();

        if(isExcludedName(name)) {
            LOG.info("Excluding Word [{}]", name);
            return;
        }

        for(NamedEntity entity : collection)
        {
            if(entity.getName().equalsIgnoreCase(name))
            {
                entity.increment();
                return;
            }
        }

        NamedEntity entity = new NamedEntity(name);

        collection.add(entity);
    }

    private void removeName(Collection<NamedEntity> collection, String name)
    {
        for(NamedEntity entity : collection) {
            if (entity.getName().equalsIgnoreCase(name) && entity.getCount() > 1) {
                entity.decrease();
                return;
            }
        }

        NamedEntity entity = new NamedEntity(name);
        collection.remove(entity);
    }

    private boolean isExcludedName(String name)
    {
        List<Exclusion> exclusions = exclusionRepository.findAll();

        if(exclusions == null)
            return false;

        if(exclusions.isEmpty())
            return false;

        for (Exclusion exclusion : exclusions)
        {
            if(exclusion.getName().equalsIgnoreCase(name))
                return true;
        }

        return false;
    }
}
