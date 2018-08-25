package application;

import java.lang.Boolean;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

public class Controller implements Initializable {

	@FXML
	private MenuItem popUp;

	@FXML
	private JFXTextField textBox;

	@FXML
	private JFXListView<LocalEvent> eventList;
	ObservableList<LocalEvent> list = FXCollections.observableArrayList();

	@FXML
	private JFXButton AddButton;

	@FXML
	private DatePicker datePicker;

	private Connection conn;

	private String[] strings;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		datePicker.setValue(LocalDate.now());
		// sql load list fron db
		try {
			int nested = 0;
			boolean completed = false;
			this.conn = connect();
			Statement m_Statement = conn.createStatement();
			String query = "SELECT * FROM todos";
		    	ResultSet m_ResultSet = m_Statement.executeQuery(query);
		    
		    	while (m_ResultSet.next()) {
		    		System.out.println(m_ResultSet.getString(1) + ", " + m_ResultSet.getString(2) + ", " + m_ResultSet.getString(3));	
		    		list.add(new LocalEvent(datePicker.getValue(), m_ResultSet.getString(1)));	
		   	 }
		    
		        eventList.setItems(list);
		        nested = Integer.parseInt(m_ResultSet.getString(2));
		    	completed = Boolean.parseBoolean(m_ResultSet.getString(3));
		    	writeData(m_ResultSet.getString(1), nested, completed, conn);
		    
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
		}
	}

	@FXML
	void submit(ActionEvent event) throws ClassNotFoundException {
		int Nested = 0;
		boolean Completed = false;
		list.add(new LocalEvent(datePicker.getValue(), textBox.getText()));
		eventList.setItems(list);
		datePicker.setValue(LocalDate.now());
		// write data, pass it Connection conn
		writeData(textBox.getText(), Nested, Completed, conn);
		textBox.setText("");
	}

	@FXML
	public void onEnter(ActionEvent event) throws ClassNotFoundException {
		int Nested = 0;
		boolean Completed = false;
		list.add(new LocalEvent(datePicker.getValue(), textBox.getText()));
		eventList.setItems(list);
		datePicker.setValue(LocalDate.now());
		// write data, pass it Connection conn
		writeData(textBox.getText(), Nested, Completed, conn);
		textBox.setText("");
	}

	@FXML
	void done(ActionEvent event) {
		int index = eventList.getSelectionModel().getSelectedIndex();
		LocalEvent localEvent = list.get(index);
		if (!localEvent.getCompleted(true)) {
			localEvent.setCompleted(true);
		}
		if (!localEvent.getCompleted(false)) {
			localEvent.setCompleted(false);
		}
		list.set(index, localEvent);
	}

	@FXML
	void remove(ActionEvent event) {
		int index = eventList.getSelectionModel().getSelectedIndex(); // starts at 0
		list.remove(index + 1); // starts at 1
		// sql remove from db	
		String sql = "DELETE FROM todo WHERE todos = '" + (index + 1) + "' ";

		try {
			conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void nest(ActionEvent event) {
		int index = eventList.getSelectionModel().getSelectedIndex();
		LocalEvent localEvent = list.get(index);
		localEvent.setNest(true);
		list.set(index, localEvent);
	}

	@FXML
	void unNest(ActionEvent event) {
		int index = eventList.getSelectionModel().getSelectedIndex();
		LocalEvent localEvent = list.get(index);
		localEvent.setNest(false);
		list.set(index, localEvent);
	}

	@FXML
	void edit(ActionEvent e) {
		System.out.println("Edit selection"); // this didn't work, btw...
		eventList.setEditable(true);
		int index = eventList.getSelectionModel().getSelectedIndex();
		eventList.scrollTo(index);
		eventList.layout();
		eventList.edit(index);
		eventList.layout();
	}

	private Connection connect() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306/todo?";
		String user = "ToDoUser";
		String password = "Rocks!";
		return DriverManager.getConnection(url, user, password);
	}

	public String[] readData(Connection conn) throws ClassNotFoundException, SQLException {
		// conn = connect();
		String sql = "SELECT * from todos";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while (rs.next()) {
			String note = rs.getString(1);
			String nestedLvl = rs.getString(2);
			String completed = rs.getString(3);
			String[] strings = { note, nestedLvl, completed };
			this.strings = strings;
		}
		return strings;
	}

	public void writeData(String note, int nested, boolean completed, Connection conn) throws ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306/todo?";
		String user = "ToDoUser"; // will one day megre with CRM
		String password = "Rocks!"; // no one uses real passwords for these dev things, do they?
		String sql = "INSERT INTO todos(todo, NestedLvl, Completed) VALUES(?,?,?)"; // "INSERT INTO mainlist(Name, name2) VALUES(?,?)";
		String nestedStr = Integer.toString(nested);
		String completedStr = Boolean.toString(completed);

		try (Connection con = DriverManager.getConnection(url, user, password);
				PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, note);
			pst.setString(2, nestedStr);
			pst.setString(3, completedStr);
			pst.executeUpdate();

			System.out.println("A new note has been inserted");

		} catch (SQLException ex) {
			ex.printStackTrace();
			// Logger lgr = Logger.getLogger(JdbcPrepared.class.getName());
			// lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}

	}
}
