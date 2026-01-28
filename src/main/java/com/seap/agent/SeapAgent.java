package com.seap.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.api.tool.Tool;
import com.embabel.common.ai.model.LlmOptions;
import com.seap.ContractRepository;
import com.seap.domain.ContractJSON;
import com.seap.domain.Enterprise;
import com.seap.domain.GeneralData;
import com.seap.domain.Vacancies;
import com.seap.domain.informacoes.*;
import com.seap.dto.StarterAgentDTO;
import com.seap.personas.Personas;
import com.seap.tools.ApiTools;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Agent(
        name = "seap-agent",
        description = "agent used to fill the values to create "
)
public class SeapAgent {


    List<Tool> webTools = Tool.fromInstance(new ApiTools());

    List<Tool> tools = Tool.safelyFromInstance(this.webTools);

    @Action
    EnterpriseInformation getEnterprise(StarterAgentDTO documentName, Ai ai) throws InterruptedException {

        System.out.println("[INFORMATION]: " + documentName);

        var info = ai
                //.withAutoLlm()
                .withDefaultLlm()
                .withPromptContributor(Personas.ENTERPRISE_SEARCHER)
                .withToolObject(new ApiTools())
                .createObject("""    
                      Document name is: '%s'
                      You need to search on vector_search_api to return values about enterprises, like values on JSON Structure.
                      For this, format a simple text to call the Tool.
                      Just call the tool using simple values (clean text) like: empresa, nome, telefone, etc...
                      """.formatted(documentName.documentName()), EnterpriseInformation.class);
        System.out.println(" [RESULT - 01]: " + info);
        Thread.sleep(30000); // waiting 30s
        return info;
    }

    @Action
    public Enterprise fillEnterprise(EnterpriseInformation info, OperationContext context) throws InterruptedException {
        var res = context.ai()
                .withLlm("gemini-2.5-flash-lite")
                .createObject("""
                Create this object using this data:
                
                Data:
                '%s'
                """.formatted(info.information()), Enterprise.class);
        System.out.println("Resultado: " + res);

        Thread.sleep(20000);

        return res;

    }

    @Action
    VacancyInformation getVacancies(StarterAgentDTO documentName, Ai ai) throws InterruptedException {

        System.out.println("[INFORMATION]: " + documentName);
        var info = ai
                .withDefaultLlm()
                .withPromptContributor(Personas.VACANCY_SEARCHER)
                .withToolObject(new ApiTools())
                .createObject("""
                      Document name is: '%s'
                      You need to search on vector_search_api to return values about vacancies, like values on JSON Structure.
                      For this, format a simple text to call the Tool.
                      Just call the tool using simple values (clean text) like: funcaoId, quantidade, vagas, etc...
                      Use all values received from search tool, 'cause is a list with 3 results
                      """.formatted(documentName.documentName()), VacancyInformation.class);

        System.out.println(" [RESULT - 02]: " + info);

        Thread.sleep(30000);

        return info;
    }

    @Action
    public Vacancies fillVacancies(VacancyInformation vacancyInfo, OperationContext context) throws InterruptedException {
        var ai = context.ai()
                .withLlm("gemini-2.5-flash-lite")
                .createObject("""
                        Create this object using this data:
                
                Data:
                '%s'
                """.formatted(vacancyInfo.information()), Vacancies.class);

        Thread.sleep(20000);
        return ai;
    }

    @Action
    GeneralInformation getGeneralInformation(StarterAgentDTO documentName, Ai ai) throws InterruptedException {

        System.out.println("[INFORMATION]: " + documentName);

        var info = ai
                //.withAutoLlm()
                .withDefaultLlm()
                .withPromptContributor(Personas.INFORMATION_SEARCHER)
                .withToolObject(new ApiTools())
                .createObject("""
                      Document name is: '%s'
                      You need to search on vector_search_api to return the general values about the contract, like values on JSON Structure.
                      For this, format a simple text to call the Tool.
                      Just call the tool using simple values (clean text) like: orgaoId, data inicio vigencia, tipo de trabalho, etc...
                      """.formatted(documentName.documentName()), GeneralInformation.class);

        Thread.sleep(30000);
        return info;
    }

    @Action
    public GeneralData getGeneralData(GeneralInformation generalInformation, OperationContext context) throws InterruptedException {
        var genData = context.ai()
                .withLlm("gemini-2.5-flash-lite")
                .createObject("""
                        Create this object using this data:
                
                Data:
                '%s'
                """.formatted(generalInformation.information()),  GeneralData.class);

        Thread.sleep(30000);

        return genData;
    }

    @Action
    public ContractJSON genContractJson(Enterprise enterprise, Vacancies vacancies, GeneralData generalData, Ai ai) throws InterruptedException {
        var result = ai
                //.withAutoLlm()
                .withLlm("gemini-2.5-flash")
                .withPromptContributor(Personas.SEARCHER)
                .withToolObject(new ApiTools())
                .createObject("""
                        Create a ContractJSon using a JSON structure and the values below
                        
                        [Enterprise]:
                        '%s'
                        
                        =======
                        [vacancies]:
                        '%s'
                        
                        ======
                        [generalData]:
                        '%s'
                        
                        For empty or null values, set to: Value Not Found
                        """.formatted(enterprise.toString(), vacancies.toString(), generalData.toString()), ContractJSON.class);
        System.out.println("Final Result: " + result);

        Thread.sleep(20000);

        return result;
    }


    @AchievesGoal(description = "Check the contract and search missing values")
    @Action
    public MissingData reviewContract(ContractJSON contractJSON, StarterAgentDTO documentName, Ai ai) {

        var missingData = ai
                .withDefaultLlm()
                .withPromptContributor(Personas.REVIEWER)
                .withToolObject(new ApiTools())
                .createObject(String.format("""
                    Analyze the following contract data: %s
                    
                    Identify all fields specifically marked as 'Value Not Found'.
                    Return a structured MissingData object containing these fields.
                    Send a email using the Tool: email_call_api and use the missing values(like string) in email body and create a short subject for email based on data;
                    subject had a max size: 255 chars
                    
                    after this, with a MissingData object, save the contract on database using a tool: database_save_contract
                    """, contractJSON.toString()), // Garanta que o toString() gere um JSON ou texto legível
                        MissingData.class);

        System.out.println(" [MISSING DATA IDENTIFIED]: " + missingData);

        if (!missingData.data().isBlank()) {

            System.out.println("LOG: Dados salvos e email enviado via Java Services.");
        }

        return missingData;
    }




}
