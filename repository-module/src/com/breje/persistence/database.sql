USE librarydb;

DROP TABLE IF EXISTS `BORROW_TBL`;
DROP TABLE IF EXISTS `USER_TBL`;
DROP TABLE IF EXISTS `BOOK_TBL`;

CREATE TABLE USER_TBL (
	user_id INT NOT NULL AUTO_INCREMENT,
    user_name VARCHAR(30) NOT NULL,
    passwd VARCHAR(30) NOT NULL,
    full_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id)
);

CREATE TABLE BOOK_TBL (
	book_id INT NOT NULL AUTO_INCREMENT,
    author VARCHAR(30) NOT NULL,
    title VARCHAR(30) NOT NULL,
    availability INT NOT NULL,
    PRIMARY KEY (book_id)
);

CREATE TABLE BORROW_TBL (
	user_id INT NOT NULL,
    book_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES USER_TBL(user_id),
    FOREIGN KEY (book_id) REFERENCES BOOK_TBL(book_id)
);
