 drop table if exists FinancialInstitution;

    create table FinancialInstitution (
        id bigint not null auto_increment,
        financialInstitutionId varchar(255),
        name varchar(255),
        organization varchar(255),
        url varchar(255),
        uuid varchar(255),
        primary key (id),
        unique (uuid)
    );
