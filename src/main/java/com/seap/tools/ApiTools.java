package com.seap.tools;

import com.embabel.agent.api.annotation.LlmTool;
import com.embabel.agent.api.tool.Tool;
import com.seap.dto.EmailResponseDTO;
import com.seap.dto.SearchResult;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ApiTools {

    private final RestClient restClient;

    public ApiTools() {
        this.restClient = RestClient.create();
    }

    @LlmTool(name = "vector_search_api", description = "get information's from vector database using a external api, using the documentName and search values")
    public SearchResult searchTool(@LlmTool.Param(required = true, description = "information used to get the values from vector database api external") String query, @LlmTool.Param(required = true, description = "name by document for use in search (documentName)") String documentName) {
        try {
            System.out.println("DATA FROM AGENT: " + query);
            System.out.println("NAME OF DOCUMENT: " + documentName);

            List<SearchResult> results = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("localhost")
                            .port(8282)
                            .path("/api/search")
                            .queryParam("documentName", documentName)
                            .queryParam("query", query)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<SearchResult>>() {});

            if (results != null && !results.isEmpty()) {
                return results.getFirst();
            }

            return null;

        } catch (Exception e) {
            System.err.println(">>> [ERRO] busca no vector deu erro: " + e.getMessage());
            return null;
        }
    }

    @LlmTool(name = "email_call_api", description = "send a email message to start/or continues a conversation by messages")
    public EmailResponseDTO emailStarterTool(
            @LlmTool.Param(required = true, description = "id used to reference the contract that is being processed") String contractId,
            @LlmTool.Param(required = true, description = "the email address of the message recipient") String to,
            @LlmTool.Param(required = true, description = "the subject of the message, the reason why this message is being sent (should be short and direct).") String subject,
            @LlmTool.Param(required = true, description = "the body of the message, everything that is said to the recipient is written here so that they can read and respond.") String body
            ) {

        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("localhost")
                            .port(9090)
                            .path("/api/emails/send")
                            .queryParam("contratoId",  contractId,
                                    "to", to,
                                    "subject", subject,
                                    "body", body)
                            .build())
                    .retrieve()
                    .body(EmailResponseDTO.class);
        }
        catch (Exception e) {
            System.err.println(">>> [ERRO] erro ao enviar email: " + e.getMessage());
            return null;
        }
    }




}
