package com.breje.repository.book.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.breje.model.Book;
import com.breje.persistence.utils.JDBCUtils;
import com.breje.persistence.utils.SQLUtils;
import com.breje.repository.book.BookRepository;

/**
 * 
 * @author Raul Breje
 *
 */
public class BookRepositoryJDBC implements BookRepository {

	@Override
	public List<Book> getAvailableBooks() {
		System.out.println("Load available books");
		Connection connection = JDBCUtils.getConnection();
		List<Book> availableBooks = new ArrayList<>();
		try {
			String sql = SQLUtils.SELECT_AVAILABLE_BOOKS;
			System.out.println("====> " + sql);
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet result = preparedStatement.executeQuery();
			while (result.next()) {
				availableBooks
						.add(new Book(result.getInt(1), result.getString(2), result.getString(3), result.getInt(4)));
			}
		} catch (SQLException e) {
			System.out.println("Error DB " + e);
		}
		return availableBooks;
	}

	@Override
	public List<Book> getUserBooks(int userId) {
		System.out.println("Load user's books");
		Connection connection = JDBCUtils.getConnection();
		List<Book> usersBooks = new ArrayList<>();
		try {
			String sql = SQLUtils.SELECT_USER_BOOKS_SQL.format(new Object[] { userId });
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet result = preparedStatement.executeQuery();
			while (result.next()) {
				usersBooks.add(new Book(result.getInt(1), result.getString(2), result.getString(3), result.getInt(4)));
			}
		} catch (SQLException e) {
			System.out.println("Error DB " + e);
		}
		return usersBooks;
	}

	@Override
	public List<Book> searchBooks(String key) {
		System.out.println("Search books");
		Connection connection = JDBCUtils.getConnection();
		List<Book> foundBooks = new ArrayList<>();
		try {
			String sql = SQLUtils.SEARCH_BOOKS_SQL.format(new Object[] { "%" + SQLUtils.toQuotedString(key) + "%" });
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet result = preparedStatement.executeQuery();
			while (result.next()) {
				foundBooks.add(new Book(result.getInt(1), result.getString(2), result.getString(3), result.getInt(4)));
			}
		} catch (SQLException e) {
			System.out.println("Error DB " + e);
		}
		return foundBooks;
	}

	@Override
	public int borrowBook(int userId, int bookId) {
		System.out.println("Borrow book");
		Connection connection = JDBCUtils.getConnection();
		int quantity = 0;
		try {
			String changeQuantitySql = SQLUtils.DEC_AVAILABILITY_BOOK_SQL.format(new Object[] { bookId });
			PreparedStatement changeQuantityStatement = connection.prepareStatement(changeQuantitySql);
			changeQuantityStatement.executeUpdate();
			String borrowSql = SQLUtils.INSERT_BORROW_SQL.format(new Object[] { userId, bookId });
			PreparedStatement borrowStatement = connection.prepareStatement(borrowSql);
			borrowStatement.executeQuery();
			String availabilitySql = SQLUtils.GET_AVAILABILITY_OF_BOOK_SQL.format(new Object[] { bookId });
			PreparedStatement availableQuantityStatement = connection.prepareStatement(availabilitySql);
			ResultSet resultSet = availableQuantityStatement.executeQuery();
			if (resultSet.next()) {
				quantity = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("Error DB " + e);
		}
		return quantity;
	}

	@Override
	public Book returnBook(int userId, int bookId) {
		System.out.println("Return book");
		Connection connection = JDBCUtils.getConnection();
		Book returned = null;
		try {
			String changeQuantitySql = SQLUtils.INC_AVAILABILITY_BOOK_SQL.format(new Object[] { bookId });
			PreparedStatement changeQuantityStatement = connection.prepareStatement(changeQuantitySql);
			changeQuantityStatement.executeUpdate();
			String returnBookSql = SQLUtils.REMOVE_BORROW_SQL.format(new Object[] { userId, bookId });
			PreparedStatement returnStatement = connection.prepareStatement(returnBookSql);
			returnStatement.executeQuery();
			String returnedBookSql = SQLUtils.GET_BOOK_SQL.format(new Object[] { bookId });
			PreparedStatement selectReturnedBook = connection.prepareStatement(returnBookSql);
			ResultSet resultSet = selectReturnedBook.executeQuery();
			if (resultSet.next()) {
				returned = new Book(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
						resultSet.getInt(4));
			}
		} catch (SQLException e) {
			System.out.println("Error DB " + e);
		}
		return returned;
	}
}
