module.exports.options = {
    host: 'localhost',
    user: 'root',
    password: 'lht1080',
    database: 'SCLAB',

    CREATE: "CREATE TABLE IF NOT EXISTS MEMBER (" +
        "NAME VARCHAR(50) NOT NULL, " +
        "ID VARCHAR(50) NOT NULL, " +
        "PW VARCHAR(100) NOT NULL, " +
        "PHNUM VARCHAR(100) NOT NULL, " +
        "EMAIL VARCHAR(100) NOT NULL, " +
        "PRIMARY KEY (ID), " +
        "UNIQUE INDEX (EMAIL)" +
        ");",

    SELECT_WHERE: "SELECT * FROM MEMBER WHERE ",
    INSERT: "INSERT INTO MEMBER VALUES",
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
