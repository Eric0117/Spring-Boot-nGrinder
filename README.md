# Spring-Boot-nGrinder
>nGrinder is a platform for stress tests that enables you to execute script creation, test execution, monitoring, and result report generator simultaneously. The open-source nGrinder offers easy ways to conduct stress tests by eliminating inconveniences and providing integrated environments."

[nGrinder](https://github.com/naver/ngrinder)는 네이버에서 제작한 오픈소스 프로젝트로 **서버의 부하 테스트를 위한 도구**입니다.
![nGrindSystemArchitecture](https://user-images.githubusercontent.com/79642391/188143168-f9ec6397-882f-406b-8b41-460695f3edd8.png)

## Environment
* Spring Boot
* Docker
* nGrinder

## Setup
### 1. nGrinder 구성
#### **다음 세가지 방법중 선호하는 방식을 선택해서 진행하면 되겠습니다.**

#### 1-1. war 파일을 이용한 구성
nGrinder war 파일을 받아서 구성하는 방법이 있습니다. [링크](https://github.com/naver/ngrinder/releases)에서 `ngrinder-controller-3.5.5-p1.war` 파일을 다운로드 후(2022년 9월 2일 기준 3.5.5-p1 버전이 최신버전입니다.)
```
$ java -jar ngrinder-controller-3.5.5-p1.war --port=8300
```
명령어를 통해 실행할 수 있습니다.

* * *

#### 1-2. Docker를 이용한 구성
다음으로 Docker를 이용한 구성입니다. [링크](https://hub.docker.com/r/ngrinder/controller)

##### Controller 설치
```
$ docker pull ngrinder/controller
```
##### Controller 실행
```
$ docker run -d -v ~/ngrinder-controller:/opt/ngrinder-controller --name controller -p 80:80 -p 16001:16001 -p 12000-12009:12000-12009 ngrinder/controller
```
Controller Port 구성
* `80` : nGrinder Controller와 통신하는 web UI port
* `9010-9019` : agent가 controller와 통신하는 포트
* `12000-12009` : controller가 해당 포트를 통해 부하 테스트를 부여


##### Agent 설치
```
$ docker pull ngrinder/agent
```
##### Agent 실행
```
$ docker run -d --name agent --link controller:controller ngrinder/agent
```

* * *

#### 1-3. Docker Compose를 이용한 구성
다음은 `docker-compose.yml`를 이용하여 구성하는 방법을 알아보겠습니다.

```
version: '3.8'
services:
  controller:
    image: ngrinder/controller
    restart: always
    ports:
      - "9000:80"
      - "16001:16001"
      - "12000-12009:12000-12009"
    volumes:
      - ./ngrinder-controller:/opt/ngrinder-controller
  agent:
    image: ngrinder/agent
    restart: always
    links:
      - controller
```

```
docker-compose up
```
`docker-compose.yml`파일의 디렉토리에서 위의 명령어로 Docker를 실행합니다.

* * *


## Run
`http://localhost:9000/login` 으로 접속하여 nGrinder Login 페이지에 접속합니다. 기본 User Id와 Password는 `admin` 입니다.

저는 `docker-compose.yml`를 이용한 세번째 방법으로 진행하였고, port 설정에 `9000`번 포트로 설정하였기에 `9000`번 포트로 접속하였습니다.
![로그인화면](https://user-images.githubusercontent.com/79642391/188143346-aaaadd66-7514-4aa8-9c48-7ad7d6c3b2b9.png)

메인 화면에서 오른쪽 상단 `admin` -> `Agent Management`에 Agent 서버가 Controller에 정상적으로 적용되었는지 확인합니다.
![AgentManagement](https://user-images.githubusercontent.com/79642391/188146803-19eddf81-11fb-4840-b84e-e9230427fea2.png)

정상적으로 Agent가 동작중이라면 화면 상단에 위치한 탭에서 `Script`를 클릭하여 스크립트 화면으로 이동한 뒤, 테스트를 위해 `+ Create` 버튼을 누르고, `Create a script`를 선택합니다.
![CreateScript1](https://user-images.githubusercontent.com/79642391/188148085-7ffc3b99-5c05-42c1-bec5-ba493a5c9ea3.png)

스크립트 생성화면에서, 스크립트 이름과 테스트 할 URL을 입력합니다.
![CreateScript2](https://user-images.githubusercontent.com/79642391/188148581-c30fa7b0-3401-4dc2-bc8a-7a974028d105.png)

IP주소는 localhost, 127.0.0.1같은 내부 IP가 아닌 Mac 기준 `시스템 환경설정` > `네트워크` 의 IP 주소, 혹은 [내 아이피 확인](https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=%EB%82%B4+%EC%95%84%EC%9D%B4%ED%94%BC)링크와 같은 외부 IP를 입력하여야 합니다.

스크립트가 생성되었다면, `Validate` 버튼으로 Script가 이상 없는지 체크합니다.
![validate](https://user-images.githubusercontent.com/79642391/188149286-b0fb2aa2-fb50-4406-84ca-43f786e7c639.png)

`Validate`가 정상적으로 진행되었다면, 화면 상단의 `Performance Test` 링크로 이동한 후, 테스트를 생성해야합니다. `Create Test` 버튼으로 테스트를 생성합니다.
![createTest1](https://user-images.githubusercontent.com/79642391/188149641-abf93da2-2d58-4106-8725-e87abf345b30.png)

![createTest2](https://user-images.githubusercontent.com/79642391/188149813-a22f3e9e-639e-49ed-98c7-96a630ce4b3d.png)

* `Agent` : Controller와 연결되어 있는 에이전트의 수 만큼 설정할 수 있습니다.
* `Vuser per agent` : 실질적으로 부하를 발생시키는 주체로 프로세스와 스레드 수를 조정하여 vUser(가상 사용자)를 생성합니다. 통상 vUser 수 = 프로세스 수 * 스레드 수 로 계산합니다.
vUser는 Controller에서 실행한 테스트 스크립트에 따라 동작하여 Target Server에 부하를 생성합니다.
* `Script` : 사용자가 해당 주소로 부하를 걸 스크립트를 보여줍니다.
* `Duration`, `Run Count` : 해당 테스트를 얼마만큼 실행할 지 설정합니다. 테스트 기간과 실행 횟수 중 하나만 선택할 수 있습니다.
* `Enable Ramp-Up` : 점차 부하를 걸 수 있는 기능입니다. 점차 부하를 가할 때, vUser를 늘리는 것이 아닌 process나 thread를 늘립니다.
* `Initial Count` : 처음 시작시 vUser의 수를 설정합니다.
* `Initial Sleep Time` : 테스트를 언제부터 실행시킬 지 설정합니다.
* `Incremental Step` : 해당 process/thread를 몇 개씩 증가시킬지 설정합니다.
* `Interval` : 설정한 것의 상승 시간을 설정합니다.

설정 값을 적용한 뒤, `Save and Start` 버튼으로 테스트를 시작합니다.
![testResult](https://user-images.githubusercontent.com/79642391/188152738-97c09dce-75ba-4510-9ec5-3b61b78a10bd.png)

간단하게 vUser를 1로 설정후 테스트 한 결과입니다.

초당 처리 가능한 요청인 `TPS`는 평균 561정도가 나왔고, 가장 TPS가 높을 때는 978이었습니다. 지연 시간은 1.27MS가 나왔습니다. 총 44,699개의 테스트 중 32,689개의 테스트가 성공했고 12,010개의 테스트가 실패했다는 정보를 얻을 수 있습니다.

* * * 


이렇게 nGrinder를 이용한 간단한 서버 부하 테스트를 진행해보았습니다.

단위 테스트 결과가 서비스 전체의 성능을 나타내진 않지만, 이를 개선함으로써 전체적인 성능을 향상시킬 수 있습니다.

서비스의 성능 지표를 알기 위해 부하 테스트는 중요하다고 생각합니다. 실제 서비스에선 여러가지 시나리오로 부하 테스트를 진행하면서 JVM 튜닝 및 슬로우 쿼리 분석 등 다양한 성능 개선 작업을 진행하는것이 꼭 필요하다고 생각합니다.

