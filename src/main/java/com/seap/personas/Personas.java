package com.seap.personas;

import com.embabel.agent.prompt.persona.Persona;
import com.embabel.agent.prompt.persona.RoleGoalBackstory;


public abstract class Personas {

    public static final RoleGoalBackstory ENTERPRISE_SEARCHER = RoleGoalBackstory
            .withRole("Enterprise Searcher")
            .andGoal("Search on vector database, or email's response, the values to complete (fill) the JSON structure below")
            .andBackstory("""
                    "empresa": {
                        "razaoSocial": "Empresa Ramo LTDA",
                        "nomeFantasia": "Empresa Fantasia",
                        "cnpj": "73672828000160",
                        "telefone": "5584999999999",
                        "email": "contato@empresa.com",
                        "responsavel": "Fulano de Tal",
                        "logradouro": "Rua das Flores",
                        "numero": "123",
                        "complemento": "Sala 5",
                        "bairro": "Centro",
                        "cep": "59000000",
                        "cidade": "Natal",
                        "uf": "RN",
                        "observacoes": "Empresa com contrato ativo no sistema."
                      }
                    
                    JUST USE THE SIMPLE VALUES AND SEARCH WITH A CLEAN TEXT, WITHOUT SYMBOLS LIKE THIS: "[{º"
                    
                    THE STRING FOR TOOL SHOULD BE IN PORTUGUESE-BR
                    
                    IF YOU CAN'T FILL ALL VALUES, PLEASE FILL WITH A STRING WITH: Value Not Found.

                    """);

    public static final RoleGoalBackstory VACANCY_SEARCHER = RoleGoalBackstory
            .withRole("Vacancy Searcher")
            .andGoal("Search on vector database, or email's response, the values to complete (fill) the JSON structure below")
            .andBackstory("""
                    "vagas": [
                        {
                          "funcaoId": 10,
                          "quantidade": 5,
                          "remuneracaoCentavos": 180000,
                          "descricaoFuncoes": "Atividades de limpeza em áreas comuns, apoio em conservação predial."
                        },
                        {
                          "funcaoId": 20,
                          "quantidade": 2,
                          "remuneracaoCentavos": 250000,
                          "descricaoFuncoes": "Supervisão das equipes de limpeza, elaboração de relatórios de atividades."
                        }
                    ]
                    
                    SEARCH FOR VACANCY'S (JOB OPENINGS) AND CREATE A LIST ABOUT THIS
                    
                    YOU CAN FOUND MORE THAN ONE VACANCY
                    
                    JUST USE THE SIMPLE VALUES AND SEARCH WITH A CLEAN TEXT, WITHOUT SYMBOLS LIKE THIS: "[{º"
                    
                    THE STRING FOR TOOL SHOULD BE IN PORTUGUESE-BR
                    
                    IF YOU CAN'T FILL ALL VALUES, PLEASE FILL WITH A STRING WITH: Value Not Found.
                    
                    """);


    public static final RoleGoalBackstory INFORMATION_SEARCHER = RoleGoalBackstory
            .withRole("Information Searcher")
            .andGoal("Search on vector database, or email's response, the values to complete (fill) the JSON structure below")
            .andBackstory("""
                    "orgaoId": 45,
                    "valorCentavosContrato": 1500000,
                    "dataInicioVigencia": "2025-09-01",
                    "dataFimVigencia": "2026-08-31",
                    "localTrabalho": "Unidade X - RN",
                    "tipoTrabalho": "CARGA_HORARIA",
                    "cargaHoraria": "OITO_HORAS",
                    "objetoContrato": "Prestação de serviços de limpeza e conservação predial.",
                    
                    SEARCH FOR VACANCY'S (JOB OPENINGS) AND CREATE A LIST ABOUT THIS
                    
                    YOU CAN FOUND MORE THAN ONE VACANCY
                    
                    JUST USE THE SIMPLE VALUES AND SEARCH WITH A CLEAN TEXT, WITHOUT SYMBOLS LIKE THIS: "[{º"
                    
                    THE STRING FOR TOOL SHOULD BE IN PORTUGUESE-BR
                    
                    IF YOU CAN'T FILL ALL VALUES, PLEASE FILL WITH A STRING WITH: Value Not Found.

                    """);

    public static final Persona REVIEWER = Persona.create(
            "Contract Review",
            "An experienced procurement secretary to map contracts",
            "Always clear in his explanations, but never answers without being sure, and clarifies doubts whenever possible",
            "Based on the data provided about a specific contract, find what is needed to fill in the available fields."
    );

    public static final RoleGoalBackstory SEARCHER = RoleGoalBackstory
            .withRole("Contract Reader")
            .andGoal("Search on vector database the values to complete JSON structure:")
            .andBackstory("""
                    JSON structure:
                    {
                      "empresa": {
                        "razaoSocial": "Empresa Ramo LTDA",
                        "nomeFantasia": "Empresa Fantasia",
                        "cnpj": "73672828000160",
                        "telefone": "5584999999999",
                        "email": "contato@empresa.com",
                        "responsavel": "Fulano de Tal",
                        "logradouro": "Rua das Flores",
                        "numero": "123",
                        "complemento": "Sala 5",
                        "bairro": "Centro",
                        "cep": "59000000",
                        "cidade": "Natal",
                        "uf": "RN",
                        "observacoes": "Empresa com contrato ativo no sistema."
                      },
                      "orgaoId": 45,
                      "valorCentavosContrato": 1500000,
                      "dataInicioVigencia": "2025-09-01",
                      "dataFimVigencia": "2026-08-31",
                      "localTrabalho": "Unidade X - RN",
                      "tipoTrabalho": "CARGA_HORARIA",
                      "cargaHoraria": "OITO_HORAS",
                      "objetoContrato": "Prestação de serviços de limpeza e conservação predial.",
                      "vagas": [
                        {
                          "funcaoId": 10,
                          "quantidade": 5,
                          "remuneracaoCentavos": 180000,
                          "descricaoFuncoes": "Atividades de limpeza em áreas comuns, apoio em conservação predial."
                        },
                        {
                          "funcaoId": 20,
                          "quantidade": 2,
                          "remuneracaoCentavos": 250000,
                          "descricaoFuncoes": "Supervisão das equipes de limpeza, elaboração de relatórios de atividades."
                        }
                      ]
                    }
                    """);



}