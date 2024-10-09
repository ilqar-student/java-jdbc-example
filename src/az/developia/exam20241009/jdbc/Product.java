package az.developia.exam20241009.jdbc;

import java.util.HashMap;

public class Product {
	private int id;
	private int category_id;
	private int units_id;
	private String name;
	private double price;
	private int stock;

	private final Category category;
	private final MeasureUnit measureUnits;
	private final HashMap<Language, Description> descriptions = new HashMap<>();

	public Product(
			final Category category, 
			final MeasureUnit units, 
			final String name, 
			final double price, 
			final int stock
			) {
		// Navigators
		this.category = category;
		this.measureUnits = units;

		// Navigator's Ids for ease of access 
		category_id = category.id();
		units_id = units.id();

		// Product [table] related values
		this.name = name;
		this.price = price;
		this.stock = stock;
	}

	private Product product() {
		return this;
	}

	public Category category() {
		return category;
	}

	public MeasureUnit Units() {
		return measureUnits;
	}
	
	public void setId(int id) throws IllegalArgumentException {
		if (id < 1) {
			throw new IllegalArgumentException("The id should be not less than 1\n\t Given number is: " + id);
		}
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getUnitsId() {
		return units_id;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public int getStock() {
		return stock;
	}

	public int getCayegoryId() {
		return category_id;
	}
	
	public Description getDescription(Language language) {
		return descriptions.get(language);
	}

	public int descriptionCount() {
		return descriptions.size();
	}

	public class Description {
		private int id;
		private int language_id;
		private int product_id;
		private String description;

		private final Language language;

		public Description(final Language language, final String description) {
			// Upper class - Main part of the Product
			this.product_id = product().id;

			// Navigators
			this.language = language;

			// Navigator's Ids for ease of access 
			this.language_id = this.language.id();

			// Description related values
			this.description = description;

			// Register this Description in its related Product class
			descriptions.put(language, this);
		}

		public Language language() {
			return this.language;
		}

		public int getId() {
			return id;
		}

		public int getProductId() {
			return product_id;
		}
		
		public int getLanguageId() {
			return language_id;
		}

		public String getDescription() {
			return description;
		}
	}
}
