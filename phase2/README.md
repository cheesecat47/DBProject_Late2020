# 실행방법 설명

과제 1-1 DDL과 1-2 insert문은 ./ddl/postgres.init.sql 파일을 사용.
postgres 컨테이너가 실행될 때 "최초 1회" 실행됨.  
```bash
$ docker-compose up -d
```

과제 1-3 쿼리문 실행하고 로그 남기기  
```bash
docker exec -it phase2_db_1 /usr/bin/psql -U team9 -d phase2 -a -f /query/query.sql 2>&1 | tee psql_log.log
# 로그 없이 실행만 하려면 query.sql까지만.
```  
  
  

# References

dkcp 파일 설정 참고  
https://www.44bits.io/ko/post/almost-perfect-development-environment-with-docker-and-docker-compose#%EB%8F%84%EC%BB%A4-%EC%BB%B4%ED%8F%AC%EC%A6%88%EB%A1%9C-%EA%B0%9C%EB%B0%9C-%ED%99%98%EA%B2%BD-%EA%B5%AC%EC%84%B1%ED%95%98%EA%B8%B0  

postgresql dockerhub  
https://hub.docker.com/_/postgres  

docker postgresql  
https://judo0179.tistory.com/48  
https://www.sysnet.pe.kr/2/0/12179  
https://dog-developers.tistory.com/132  

postgres 스크립트 파일 실행  
https://acpi.tistory.com/76  