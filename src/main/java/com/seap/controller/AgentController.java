package com.seap.controller;

import com.embabel.agent.core.Agent;
import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.core.AgentProcess;
import com.embabel.agent.core.ProcessOptions;
import com.seap.ProcessingValues;
import com.seap.dto.AgentCallbackPayload;
import com.seap.dto.StarterAgentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent")
public class AgentController {

    private final AgentPlatform agentPlatform;

    private final String AGENT_NAME = "seap-agent";

    public AgentController(AgentPlatform agentPlatform) {
        this.agentPlatform = agentPlatform;
    }

    @PostMapping("/newDocument")
    public ResponseEntity<?> starByNewDocument(@RequestBody StarterAgentDTO documentName){
        System.out.println("Novo contrato recebido com id: " + documentName.documentName());

        Agent agent = agentPlatform.agents().stream()
                .filter(a -> a.getName().toLowerCase().contains(AGENT_NAME))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Agente nao encontrado"));

        AgentProcess agentProcess = agentPlatform.createAgentProcessFrom(
                agent,
                ProcessOptions.DEFAULT,
                documentName
        );

        new ProcessingValues(
                agentProcess,
                "Start agent to process new document",
                documentName.documentName()
        );

        agentProcess.run();

        return ResponseEntity.ok().build();

    }


    @PostMapping("/callback")
    public void callback(@RequestBody AgentCallbackPayload payload, Model model) {
        System.out.println("Resposta recebida do email para o contrato: " + payload.contratoId());

        Agent agent = agentPlatform.agents().stream()
                .filter(a -> a.getName().toLowerCase().contains(AGENT_NAME))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Agente nao encontrado"));

        AgentProcess agentProcess = agentPlatform.createAgentProcessFrom(
                agent,
                ProcessOptions.DEFAULT,
                payload
        );

        new ProcessingValues(
                agentProcess,
                "Update JSON using email response",
                payload.resposta()
        ).addToModel(model);

        agentProcess.run();
    }
}
