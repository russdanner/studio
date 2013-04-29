var CStudio = {

    cstudio_TAX_CORP_KEYWORD: "cstudio-core-web:corporateKeyword",

    /**
     * process corp. keywords looks at the inbound xml document and performs 
     * the following operations
     * 1. determines new list of keywords based on topics
     * 2. compares all manual keywords to the managed keywords - removing 
     *    duplicatated manual keywords.
     * 3. re-writes the XML
     * 
     * Assimptions: 
     * - xml structure is consistent across all consumers of this utility
     *   the manual keyords are stored as follows:
     *       /meta-keywords
     *       <meta-keywords>keywordsA, keyword b, keyword C</meta-keywords>
     *
     *   the managed topics are stored as follows:
     *       /corporateKeywords//keyword
     *       <corporateKeywords>
     *          <keyword label="virtual computing" 
     *                nodeRef="workspace://SpacesStore/8512f23a-6ddc-4b17-8555-0578741c6997">1002</keyword>
     *       </corporateKeywords>
     *
     *   the managed list of keywords are stored as:
     *       /managedKeywords//keyword
     *       <managedKeywords>
     *          <keyword>keyword</keyword>
     *       <managedKeywords>
     */
    processCorpKeywords: function(xml) {

        try {
            // step 1.
            var keywordIdx = 0;
            var managedKeywords = new Array();
            var keywordsTaxonomy = removeXmlDocType(loadTaxonomyInstance(siteId, this.cstudio_TAX_CORP_KEYWORD, true).xml);
            var tXml = new XML(keywordsTaxonomy);

            var xTopics = xml.corporateKeywords.keyword;
            for(xtIdx in xTopics) {
                 var xTopic = xTopics[xtIdx];

                // get the listed topic from the taxonomy with all of it's keywords
                // for some reason the e4x syntax below doesn't work so this crude code below does the job instead                
                //var tTopic = keywordsTaxonomy..*.(function::attribute("label") == xTopic.@label);
                var tTopic = null;
                var tTopics = tXml.corporateKeyword;

                for(tTopicIdx in tTopics) {
                    var cTopic = tTopics[tTopicIdx];

                    if(cTopic.@label.toString() == xTopic.@label.toString()) {
                        tTopic = cTopic;
                        break;
                    }
                } // end crude workaround

                // now add all the keywords to the list for each topic
                if(tTopic) {
//consoleLogger.debug("\t processing topic for keywords:"+tTopic.corporateKeyword.length);
//                    var tKeywords = tTopic.corporateKeyword;
//                    
//                    if(tKeywords) {
//                        for(tKeywordIdx in tKeywords) {
//                            tKeyword = tKeywords[tKeywordIdx]
//
//consoleLogger.debug("\t processing keyword :"+tKeyword.@label);
//                            managedKeywords[keywordIdx] = tKeyword;
//                            keywordIdx++;
//                        }
//                    }
                    
                    // adding topics as the keyword since taxonomy is still one level deep
                    managedKeywords[keywordIdx] = tTopic; 
                    keywordIdx++;
                }
            }

            // step 2: de-dupe
            var matched = false;
            var dedupedKeywordsIdx = 0;
            var dedupedKeywords = new Array();
            var manualKeywords = xml['meta-keywords'].split(',');

            for(mkIdx in manualKeywords) {
                var manualKeyword = manualKeywords[mkIdx].replace(/^\s\s*/, '').replace(/\s\s*$/);

                for(var i=0; i < managedKeywords.length; i++) {
                    var managedKeyword = managedKeywords[i].@label.toString();

                    if(managedKeyword == manualKeyword) {
                        matched = true;
                        break;
                    }
                }
                
                if(!matched) {
                   dedupedKeywords[dedupedKeywordsIdx] = manualKeyword;
                   dedupedKeywordsIdx++;
                }
                
                matched = false;
            }
              
            // step 3.
            xml['meta-keywords'] = dedupedKeywords.join(', ');
            xml.managedKeywords = new XML("<managedKeywords></managedKeywords>");
            
            for(var j=0; j < managedKeywords.length; j++) {
                var keyword = managedKeywords[j];
                
                var element = "<keyword label='" + keyword.@label.toString() + "' " +
                                       "value='" + keyword.@value.toString() + "'>" + 
                                       "name='" + keyword.@name.toString() + "'>" +
                                       "decription='" + keyword.@description.toString() + "'>" + 
                                          keyword.toString() + 
                                       "</keyword>";
                                       
                xml.managedKeywords.appendChild(new XML(element));
            }
        }
        catch(err) {
            // no change to the xml document on error
            consoleLogger.debug("error while processing corp keywords :" + err);
        }
        
        return xml;
    }
};
