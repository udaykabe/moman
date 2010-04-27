{
    uuid=substr($1,31,32)

    sub("</name", "", $3)
    name=$3
    
    sub("</url", "", $5)
    url=$5

    sub("</fid", "", $7)
    fid=$7
    
    sub("</org", "", $9)
    org=$9
    
    printf("insert into FinancialInstitution (financialInstitutionId, name, organization, url, uuid) values ('%s', '%s', '%s', '%s', '%s');\n", fid, name, org, url, uuid)
}
