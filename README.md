InfraReport – Sistema de Gerenciamento de Chamados de Infraestrutura

Back-end desenvolvido em Java e Spring Boot

1. Introdução

O InfraReport é um sistema de gerenciamento e acompanhamento de chamados de infraestrutura, desenvolvido com foco em ambientes institucionais, como escolas, faculdades e empresas.
O objetivo principal é fornecer uma plataforma organizada para registro, categorização, visualização e resolução de problemas estruturais, além de permitir interação entre usuários, equipes técnicas e administradores.

Este repositório contém o back-end do sistema, implementado em Java + Spring Boot, seguindo boas práticas de desenvolvimento, uso de DTOs, serviços desacoplados e arquitetura REST.

2. Objetivos do Sistema

O InfraReport visa:

Facilitar o processo de abertura e acompanhamento de chamados.

Organizar chamados por categoria, status, localização (andares) e equipe responsável.

Possibilitar comunicação entre usuários e equipe técnica através de comentários.

Permitir upload e consulta de imagens do chamado.

Manter registro histórico do ciclo de vida do chamado.

Oferecer um ambiente seguro de autenticação e recuperação de senha.

3. Arquitetura do Sistema
3.1. Tecnologias Utilizadas

Java 17+

Spring Boot

Spring Web

Spring Data JPA

Spring Security

Maven

MySQL / PostgreSQL (dependendo da configuração do ambiente)

Docker e Docker Compose

JPA/Hibernate

3.2. Estrutura de Pastas (resumida)
src/main/java/br/edu/lampi/infrareport/
 ├── controller/        → Endpoints REST
 ├── service/           → Regras de negócio
 ├── repository/        → Acesso ao banco (JPA)
 ├── model/             → Entidades
 ├── controller/dto/    → Objetos de transferência (DTOs)
 └── config/            → Configurações gerais

4. Funcionalidades Principais
4.1. Módulo de Usuários

Autenticação e autorização.

Login com credenciais.

Recuperação de senha.

Gestão de perfis.

4.2. Módulo de Chamados

Abertura de chamados contendo:

descrição

categoria

equipe responsável

localização

prioridade

Mudança de status através do ciclo do chamado.

Upload de imagens.

Comentários e interação no chamado.

Classificação final e feedback.

4.3. Módulo Administrativo

Cadastro e gestão de:

Categorias

Andares

Equipes

Status de chamados

5. Requisitos de Ambiente
5.1. Requisitos Mínimos

Java 17+

Maven 3.8+

Banco de dados relacional configurado

Docker (opcional, mas recomendado)

6. Instalação e Execução
6.1. Clonar o Repositório
git clone https://github.com/seu-repositorio/infrareport-back-end.git
cd infrareport-back-end

6.2. Executar com Maven
mvn clean install
mvn spring-boot:run

6.3. Executar com Docker
docker-compose up --build

7. Configuração do Banco de Dados

O arquivo application.properties ou application.yml deve conter:

spring.datasource.url=jdbc:mysql://localhost:3306/infrareport
spring.datasource.username=root
spring.datasource.password=senha
spring.jpa.hibernate.ddl-auto=update

8. Endpoints (Visão Geral)
Usuários

POST /auth/login

POST /auth/forgot-password

POST /auth/reset-password

Chamados

POST /calls

GET /calls

GET /calls/{id}

POST /calls/{id}/status

POST /calls/{id}/comments

POST /calls/{id}/images

Administração

POST /categories

POST /teams

POST /floors

POST /call-status

A documentação detalhada dos endpoints pode ser estendida com Swagger, Postman ou Apidocs, caso desejado.

9. Estrutura dos Principais DTOs
Exemplo – CallRequestDTO
public class CallRequestDTO {
    private String title;
    private String description;
    private Long categoryId;
    private Long teamId;
    private Long floorId;
}

Exemplo – UserLoginDTO
public class LoginRequestDTO {
    private String email;
    private String password;
}

10. Fluxo Geral do Sistema
1. Abertura de chamado

Usuário envia o formulário com detalhes → sistema cria o chamado.

2. Equipe técnica assume

Chamado recebe novo status.

3. Comunicação

Comentários, imagens e atualizações são registrados.

4. Finalização

Equipe encerra o chamado.

Usuário pode classificar e deixar feedback.