package users_backlog.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import users_backlog.models.Idea;
import users_backlog.models.Implementation;
import users_backlog.models.Model;


@Service
public class ElasticSearchService {
    
    private static final Logger log = Logger.getLogger(ElasticSearchService.class.getName());

    @Autowired private RestHighLevelClient client;

    public void index(Model model) {
        try {
            IndexRequest request = new IndexRequest(model.getClassName())
                .id(String.valueOf(model.getId()))
                .source(model.toMap(), XContentType.JSON);

            client.index(request, RequestOptions.DEFAULT);

        } catch (IOException e) {
            log.severe(e.getMessage());
        } catch (ElasticsearchException e) {
            log.severe(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    public void get() {
        try {
            GetRequest getRequest = new GetRequest("implementation", "idea");

            client.get(getRequest, RequestOptions.DEFAULT);
            // String source = getResponse.getSourceAsString();

        } catch (IOException e) {
            log.severe(e.getMessage());
        } catch (ElasticsearchException e) {
            log.severe(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    public void delete(Model model) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(model.getClassName().toLowerCase())
                .id(String.valueOf(model.getId()));

            client.delete(deleteRequest, RequestOptions.DEFAULT);

        } catch (IOException e) {
            log.severe(e.getMessage());
        } catch (ElasticsearchException e) {
            log.severe(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    public void update(Model model) {
        try {
            UpdateRequest updateRequest = new UpdateRequest(model.getClassName(), String.valueOf(model.getId()))
                .upsert(model.toMap(), XContentType.JSON);

            client.update(updateRequest, RequestOptions.DEFAULT);

        } catch (IOException e) {
            log.severe(e.getMessage());
        } catch (ElasticsearchException e) {
            log.severe(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    public List<Model> search(String criteria) {
        List<Model> models = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest("implementation");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);

        try {
            DisMaxQueryBuilder qb = QueryBuilders.disMaxQuery();
            qb.add(QueryBuilders.matchQuery("name", criteria));
            qb.add(QueryBuilders.fuzzyQuery("name", criteria));
            for (String subCriteria: criteria.split(" ")) {
                qb.add(QueryBuilders.matchQuery("name", subCriteria));
                qb.add(QueryBuilders.fuzzyQuery("name", subCriteria));
            }
            searchSourceBuilder.query(qb);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            for (SearchHit hit: searchHits) {
                try {
                    models.add(Implementation.fromMap(hit.getSourceAsMap()));
                } catch (Exception e) {
                    log.severe(e.getMessage());
                }
            }

        } catch (IOException e) {
            log.severe(e.getMessage());
        } catch (ElasticsearchException e) {
            log.severe(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }

        return models;
    }

    public List<Idea> searchForIdeas(String partialSummary) {
        List<Idea> ideas = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest("idea");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);

        try {
            DisMaxQueryBuilder qb = QueryBuilders.disMaxQuery();
            qb.add(QueryBuilders.matchQuery("summary", partialSummary));
            qb.add(QueryBuilders.fuzzyQuery("summary", partialSummary));
            for (String subCriteria: partialSummary.split(" ")) {
                qb.add(QueryBuilders.matchQuery("summary", subCriteria));
                qb.add(QueryBuilders.fuzzyQuery("summary", subCriteria));
            }
            searchSourceBuilder.query(qb);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            for (SearchHit hit: searchHits) {
                try {
                    ideas.add(Idea.fromMap(hit.getSourceAsMap()));
                } catch (Exception e) {
                    log.severe(e.getMessage());
                }
            }

        } catch (IOException e) {
            log.severe(e.getMessage());
        } catch (ElasticsearchException e) {
            log.severe(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }

        return ideas;
    }

}