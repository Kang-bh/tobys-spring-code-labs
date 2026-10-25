# 토비의 스프링 코드 실습

책의 개념을 직접 실행하고 테스트하는 저장소다. 원문을 그대로 옮긴 코드는 아니며, 현재 예제는 Java 17과 Spring Framework 6.1.13으로 작성했다.

| 장 | 실습 내용 | 코드 |
| --- | --- | --- |
| 1장 오브젝트와 의존관계 | 1.4 DaoFactory, 1.6 싱글톤 레지스트리와 오브젝트 스코프 | [chapter-01](chapter-01/README.md) |

Java 17 이상과 Maven 3.9 이상이 필요하다. 저장소 루트에서 `mvn test`를 실행하면 모든 장의 테스트를 실행한다.

```bash
mvn test
mvn -pl chapter-01 -q compile exec:java -Dexec.mainClass=study.toby.chapter01.dao.Demo
mvn -pl chapter-01 -q compile exec:java -Dexec.mainClass=study.toby.chapter01.scope.ScopeDemo
```

각 장은 별도의 Maven 모듈이다. 한 장 안에서는 앞 절의 예제를 다음 절에서 재사용한다. 새 장을 추가할 때는 별도 모듈을 만들고 루트 `pom.xml`의 `modules`에 등록한다.
