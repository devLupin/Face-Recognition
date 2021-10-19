module.exports.options = {
    host: 'localhost',
    user: 'root',
    password: 'lht1080',
    database: 'SCLAB',

    CREATE: "CREATE TABLE IF NOT EXISTS MEMBER (" +
        "NAME VARCHAR(500) NOT NULL, " +
        "ID VARCHAR(500) NOT NULL, " +
        "PW VARCHAR(500) NOT NULL, " +
        "PHNUM VARCHAR(500) NOT NULL, " +
        "EMAIL VARCHAR(500) NOT NULL, " +
        "PRIMARY KEY (ID), " +
        "UNIQUE INDEX (EMAIL, PHNUM)" +
        ");",

    SELECT_WHERE_ID: "SELECT ID FROM MEMBER WHERE ",
    SELECT_WHERE_PW: "SELECT PW FROM MEMBER WHERE ",
    SELECT_WHERE_LOGIN: "SELECT ID, PW FROM MEMBER WHERE ",

    INSERT: "INSERT INTO MEMBER VALUES(",
};

//select 결과가 없다면 [] 이렇게 나옴
//select 결과가 있다면
/*
[
  RowDataPacket {
    NAME: 'dd',
    ID: 'dd',
    PW: '1234',
    PHNUM: '159159',
    EMAIL: 'aegwfw'
  }
]
*/
