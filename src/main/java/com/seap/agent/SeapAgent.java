package com.seap.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.api.tool.Tool;
import com.seap.domain.ContractJSON;
import com.seap.domain.Enterprise;
import com.seap.domain.GeneralData;
import com.seap.domain.Vacancies;
import com.seap.domain.informacoes.ContractId;
import com.seap.domain.informacoes.EnterpriseInformation;
import com.seap.domain.informacoes.GeneralInformation;
import com.seap.domain.informacoes.VacancyInformation;
import com.seap.dto.StarterAgentDTO;
import com.seap.personas.Personas;
import com.seap.tools.ApiTools;

import java.util.List;


@Agent(
        name = "seap-agent",
        description = ""
)
public class SeapAgent {

    List<Tool> webTools = Tool.fromInstance(new ApiTools());

    List<Tool> tools = Tool.safelyFromInstance(this.webTools);

    @Action
    EnterpriseInformation getEnterprise(StarterAgentDTO documentName, Ai ai) {

        System.out.println("[INFORMATION]: " + documentName);
        var info = ai
                .withAutoLlm()
                .withPromptContributor(Personas.ENTERPRISE_SEARCHER)
                .withToolObject(new ApiTools())
                .createObject("""    
                      Document name is: '%s'
                      You need to search on vector_search_api to return values about enterprises, like values on JSON Structure.
                      For this, format a simple text to call the Tool.
                      Just call the tool using simple values (clean text) like: empresa, nome, telefone, etc...
                      """.formatted(documentName.documentName()), EnterpriseInformation.class);
        System.out.println(" [RESULT - 01]: " + info);
        return info;
    }

    @Action
    public Enterprise fillEnterprise(EnterpriseInformation info, OperationContext ai){
        var res = ai.promptRunner().createObject("""
                Create this object using this data:
                
                Data:
                '%s'
                """.formatted(info.information()), Enterprise.class);
        System.out.println("Resultado: " + res);

        return res;
    }

    @Action
    VacancyInformation getAddress(StarterAgentDTO documentName, Ai ai) {

        System.out.println("[INFORMATION]: " + documentName);
        var info = ai
                .withAutoLlm()
                .withPromptContributor(Personas.VACANCY_SEARCHER)
                .withToolObject(new ApiTools())
                .createObject("""
                      Document name is: '%s'
                      You need to search on vector_search_api to return values about vacancies, like values on JSON Structure.
                      For this, format a simple text to call the Tool.
                      Just call the tool using simple values (clean text) like: funcaoId, quantidade, vagas, etc...
                      """.formatted(documentName.documentName()), VacancyInformation.class);

        System.out.println(" [RESULT - 02]: " + info);

        return info;
    }

    @Action
    public Vacancies fillVacancies(VacancyInformation vacancyInfo, OperationContext context) {
        return context.ai()
                .withAutoLlm()
                .createObject("""
                        Create this object using this data:
                
                Data:
                '%s'
                """.formatted(vacancyInfo.information()), Vacancies.class);
    }

    @Action
    GeneralInformation getGeneralInformation(StarterAgentDTO documentName, Ai ai) {

        System.out.println("[INFORMATION]: " + documentName);

        return ai
                .withAutoLlm()
                .withPromptContributor(Personas.INFORMATION_SEARCHER)
                .withToolObject(new ApiTools())
                .createObject("""
                      Document name is: '%s'
                      You need to search on vector_search_api to return the general values about the contract, like values on JSON Structure.
                      For this, format a simple text to call the Tool.
                      Just call the tool using simple values (clean text) like: orgaoId, data inicio vigencia, tipo de trabalho, etc...
                      """.formatted(documentName.documentName()), GeneralInformation.class);
    }

    @Action
    public GeneralData getGeneralData(GeneralInformation generalInformation, OperationContext context) {
        return context.ai()
                .withAutoLlm()
                .createObject("""
                        Create this object using this data:
                
                Data:
                '%s'
                """.formatted(generalInformation.information()),  GeneralData.class);
    }

    @AchievesGoal(description = "Create a ContractJson Object using the datas searched on the agent flux")
    @Action
    public ContractJSON genContractJson(Enterprise enterprise, Vacancies vacancies, GeneralData generalData, Ai ai) {
        var result = ai
                .withAutoLlm()
                .withPromptContributor(Personas.SEARCHER)
                .createObjectIfPossible("""
                        Create a ContractJSon using a JSON structure and the values you know
                        """, ContractJSON.class);
        System.out.println("Final Result: " + result);

        return result;
    }





}
