package az.developia.exam20241009.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

public class Main {
	static String jdbcUrl = "jdbc:mysql://localhost:3306/java19";
	static String username = "root";
	static String password = "12345";

	static HashSet<Language> languages = new HashSet<>();

	public static void main(String[] args) {

		// Gathering important data that the Product and its Description depends
		Category category = new Category(1, "Vegetables");
		MeasureUnit units = new MeasureUnit(1, "kg", "Kilograms");
		Language languageEn = new Language(1, "en", "English");
		Language languageAz = new Language(2, "az", "Azerbaijan");

		// Populate the collection of Languages
		languages.add(languageEn);
		languages.add(languageAz);

		// Product main data
		Product product = new Product(category, units, "Cauliflower", 4.0, 1200);

		// Product's Description in English
		product.new Description(languageEn, "Cruciferous vegetable");

		// Product's Description in Azerbaijan
		product.new Description(languageAz, "Gül-Kələm - Cruciferous fəsiləsindən olan tərəvəz");

		try {
			addProduct(product);
			// readProducts();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static void addProduct(Product product) throws SQLException {
		Connection connection = DriverManager.getConnection(jdbcUrl, username, password);


		// INSERT Product Main data
		String insertProductSQL = "INSERT INTO product (id, category_id, units_id, name, price, stock) VALUES (?, ?, ?, ?, ?, ?);";
		PreparedStatement productStatement = connection.prepareStatement(insertProductSQL, Statement.RETURN_GENERATED_KEYS);
		productStatement.setInt(1, 0);
		productStatement.setInt(2, product.getUnitsId());
		productStatement.setInt(3, product.getCayegoryId());
		productStatement.setString(4, product.getName());
		productStatement.setDouble(5, product.getPrice());
		productStatement.setInt(6, product.getStock());

		// GET the Generated Product Id
		int insertedProducts = productStatement.executeUpdate();

		// Report the Result of adding the Product's main data
		if (insertedProducts == 0) {
			System.out.println("Product was not added!");
			System.out.println();
			return;
		}
		System.out.println("Product's main data Successfully added !");
		System.out.println("\tProduct Name: " + product.getName() + ", Price: " + product.getPrice());
		System.out.println();


		// Keys [or Ids of] Recently added/inserted rows
		ResultSet keys = productStatement.getGeneratedKeys();
		// In this case [for this example] there was only one row [of Products] were added
		// so, needs to get the only element in the set; which is the Key/Id of the Product
		try {
			if (keys.next()) {				
				product.setId(keys.getInt(1));
				System.out.println("\tINSERTED Product ID: " + product.getId());
				System.out.println();
			}
		}
		catch (IllegalArgumentException e) {
			System.out.println();
			System.out.println("Description was not added ! The reason described below:");
			System.out.println(e);
			System.out.println();
			return;
		}


		// INSERT Product's Descriptions
		String insertDescriptionSQL = "INSERT INTO product_description (id, language_id, product_id, description) VALUES (?, ?, ?, ?);";
		PreparedStatement descriptionStatement;

		int i = 0;
		for (Language lang : languages) {

			i++;
			Product.Description description = product.getDescription(lang);
			if (description == null) {
				continue;
			}

			descriptionStatement = connection.prepareStatement(insertDescriptionSQL);
			descriptionStatement.setInt(1, 0);
			descriptionStatement.setInt(2, description.getLanguageId());
			descriptionStatement.setInt(3, product.getId());
			descriptionStatement.setString(4, description.getDescription());

			int resultInsertDescription = descriptionStatement.executeUpdate();

			if (resultInsertDescription == 0) {
				System.out.println();
				System.out.println("\tProduct Description was not added! N: " + i);
				System.out.println("\t\t Description: " + description.getDescription());
				System.out.println("\t\t Language Code: " + description.language().name());
				System.out.println("\t\t Language Id: " + description.getLanguageId());
				System.out.println("\t\t Product Id: " + product.getId());
				System.out.println();
				return;
			}

			System.out.println("\t[ " + i + " ] Product's Description Successfully added !");
			System.out.println("\t\t Language: " + lang.code() + "\n\t\t Description: " + description.getDescription());
			System.out.println();
		}
		System.out.println("\tTotal Descriptions: " + product.descriptionCount() + "; Added Descriptions: " + i);

		connection.close();
	}

	static void readProducts() throws SQLException {
		System.out.println("Example of Database connection and retrieval of data using SQL");

		Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

		Statement statement = connection.createStatement();
		String sql = "SELECT * FROM product";

		ResultSet result = statement.executeQuery(sql);

		String name = "";
		String price = "";
		while (result.next()) {
			name = result.getString("name");
			price = result.getString("price");
			System.out.println("Product Name: " + name + ", Price: " + price);
		}
		System.out.println();

		connection.close();
	}

}
