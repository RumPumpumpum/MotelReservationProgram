# 프로젝트 제목

🏨 모텔 객실 예약 프로그램 💼
<BR><BR><BR>
## 소개
자바프로그래밍 강의의 기말 프로젝트 수행을 통해 만들어진 프로그램 입니다.<BR>
5.20 ~ 6.10 기간동안 작업하였습니다.<BR>

객실 예약, 예약 확인, 예약 편집, 예약 삭제, 기준날짜 변경, 체크아웃 시간 변경, 오늘 주차정보, 로그출력 기능 등을 구현<BR><BR>
<체크아웃 시간이란?><BR>
현재 시간이 설정한 체크아웃 시간 이전 이라면 오늘 들어올 예약자를 입실예정으로 표시하고<BR>
어제 머문 예약자가 있다면 퇴실예정으로 표시한다<BR>
![image](https://github.com/RumPumpumpum/MotelReservationProgram/assets/104812604/da91ca16-ae25-43a2-b5ec-2d54bab5affc)
![image](https://github.com/RumPumpumpum/MotelReservationProgram/assets/104812604/f15a930a-7f89-4a51-9d71-0efc57ca2118)
<BR><BR>체크아웃 시간이 지났다면 퇴실처리 및 입실처리<BR>
![image](https://github.com/RumPumpumpum/MotelReservationProgram/assets/104812604/feafa599-c845-402b-96e9-7a60dcd80fab)
<BR><BR>또한 같은 날짜에 입실예정과 퇴실예정 날짜가 있다면 동시에 출력한다 <BR>
  ![image](https://github.com/RumPumpumpum/MotelReservationProgram/assets/104812604/b33d7ce4-c604-4d3b-8784-71d6831154d5)

 !예외사항<BR>
  현재 날짜가 아닌 다른 날짜를 볼 때에는 체크아웃 시간과 관계 없이 당일 입실중인 예약을 표시<BR>
  해당 날짜에 퇴실예정인 방은 표시하지 않음


### 사전 요구사항📦

ReservationDB.java
```
    private static final String DB_URL = "jdbc:mysql://localhost:3306/reservationprogram";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin";
```
  해당부분의 내용을 본인의 환경에 맞게 설정해 주세요.


## 실행화면
  프로그램 메인화면<BR>
![image](https://github.com/RumPumpumpum/MotelReservationProgram/assets/104812604/72bbfb97-ef23-46fc-a3bf-e163ff00adc8)
  
  객실예약<BR>
![image](https://github.com/RumPumpumpum/MotelReservationProgram/assets/104812604/69b65dcb-f3f1-4ac4-9896-bfac470a507e)
  
  예약정보 확인<BR>
![image](https://github.com/RumPumpumpum/MotelReservationProgram/assets/104812604/87091c9a-b0b7-4b6b-900e-13fd09059c86)

  예약 정보 편집<BR>
  ![image](https://github.com/RumPumpumpum/MotelReservationProgram/assets/104812604/265525fd-aa7e-4067-90e6-43365ffb691e)

  예약 정보 삭제<BR>
  ![image](https://github.com/RumPumpumpum/MotelReservationProgram/assets/104812604/ac956534-5304-45f2-82c9-9ebe0f34250c)
       
  예약 내역 실시간 표시<BR>
  ![image](https://github.com/RumPumpumpum/MotelReservationProgram/assets/104812604/eec90b03-385d-44e3-9002-ba4db62a9fe4)
  
  오늘 주차한 차 정보 확인<BR>
![image](https://github.com/RumPumpumpum/MotelReservationProgram/assets/104812604/863226a2-8703-432c-be15-da93dd46851b)
  
  메인에 표시할 날짜 변경<BR>
![image](https://github.com/RumPumpumpum/MotelReservationProgram/assets/104812604/46202c73-7200-4deb-952f-a83c2dfbb16f)
  
  체크아웃 시간 변경<BR>
  ![image](https://github.com/RumPumpumpum/MotelReservationProgram/assets/104812604/c70b8a02-505e-43f1-b2dd-4694e56bae26)

  로그 표시<BR>
  ![image](https://github.com/RumPumpumpum/MotelReservationProgram/assets/104812604/6758d5d9-cecc-4bc1-98e1-724ce991cf46)



