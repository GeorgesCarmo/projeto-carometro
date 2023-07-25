package model;

import java.sql.Connection; // responsável por abrir e fechar a conexão com o banco de dados
import java.sql.DriverManager; // tipo de banco de dados

public class DAO { // classe responsável pela comunicação com o banco de dados

	private Connection con;
	private String driver = "com.mysql.cj.jdbc.Driver";
	private String url = "jdbc:mysql://localhost:3306/dbcarometro";
	private String user = "root";
	private String password = "1234567";

	public Connection conectar() {
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			return con;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
}
