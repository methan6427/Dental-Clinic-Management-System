package com.example.phase4_1220813_122856_1210475;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;

public class ProductManagement {
	
    private Tab emptyTab; 

	dataBaseConnection db = new dataBaseConnection();
	ObservableList<Product> productList = FXCollections.observableArrayList();
	TableView<Product> productTableView = new TableView<>();

	public void readProduct() {
		productList.clear();
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT * FROM product";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				productList.add(new Product(rs.getInt("productId"), rs.getString("productName"),
						rs.getString("category"), rs.getInt("reorderLevel"), rs.getInt("quantity"),
						rs.getDouble("unitPrice"), rs.getString("description"), rs.getInt("stockId")));
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		productTableView.setItems(productList);
	}

	public BorderPane createProductManagementLayout(Stage primaryStage, Scene homeScene) {
	    BorderPane productRoot = new BorderPane();
	    productRoot.setPrefSize(800, 600);
	    productRoot.setStyle("-fx-background-color: #15919B;");

	    TabPane tabPane = new TabPane();

	    Tab productTab = new Tab("Operation Products");
	    productTab.setClosable(false);

	    Label titleLabel = new Label("Product Management");
	    titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
	    titleLabel.setAlignment(Pos.CENTER); 
	    VBox titleBox = new VBox(titleLabel);
	    titleBox.setAlignment(Pos.CENTER); 
	    titleBox.setPadding(new Insets(10));

	    productTableView.setEditable(true);
	    productTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

	    if (productTableView.getColumns().isEmpty()) {
	        TableColumn<Product, Integer> idCol = new TableColumn<>("Product ID");
	        idCol.setCellValueFactory(new PropertyValueFactory<>("productId"));

	        TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
	        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

	        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
	        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

	        TableColumn<Product, Integer> reorderCol = new TableColumn<>("Reorder Level");
	        reorderCol.setCellValueFactory(new PropertyValueFactory<>("reorderLevel"));

	        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Quantity");
	        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

	        TableColumn<Product, Double> priceCol = new TableColumn<>("Unit Price");
	        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

	        TableColumn<Product, String> descriptionCol = new TableColumn<>("Description");
	        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

	        TableColumn<Product, Integer> stockIdCol = new TableColumn<>("Stock ID");
	        stockIdCol.setCellValueFactory(new PropertyValueFactory<>("stockId"));

	        productTableView.getColumns().addAll(idCol, nameCol, categoryCol, reorderCol, quantityCol, priceCol,
	                descriptionCol, stockIdCol);
	    }

	    productTableView.setItems(productList);

	    HBox buttonLayout = new HBox(10);
	    buttonLayout.setAlignment(Pos.CENTER);
	    buttonLayout.setPadding(new Insets(10));

	    Button searchProductButton = new Button("Search for Product");
	    TextField searchProductField = new TextField();
	    searchProductField.setPromptText("Enter Product ID");
	    Button homeProductButton = new Button("Home");
	    Button refreshProductButton = new Button("Refresh");
	    refreshProductButton.setOnAction(e -> showSearchAndEditDialog());	 
	    homeProductButton.setOnAction(e -> primaryStage.setScene(homeScene));

	    searchProductButton.setOnAction(e -> {
	        String idText = searchProductField.getText();
	        if (!idText.isEmpty()) {
	            int id = Integer.parseInt(idText);
	            searchProduct(id);
	        }
	    });

	    buttonLayout.getChildren().addAll(homeProductButton, searchProductField, searchProductButton,refreshProductButton);

	    VBox contentBox = new VBox(20, titleBox, productTableView, buttonLayout); 
	    contentBox.setPadding(new Insets(10));

	    productTab.setContent(contentBox);

	    Tab statisticsTab = new Tab("Statistics");
	    statisticsTab.setContent(createAdvancedStatisticsLayout());
	    statisticsTab.setClosable(false);


	    emptyTab = new Tab("Product Image");
	    emptyTab.setClosable(false);
	    VBox vbox = new VBox(30);
	    vbox.getStyleClass().add("vbox");
	    emptyTab.setContent(vbox); 
	    tabPane.getTabs().add(emptyTab); 


		String[][] imageData = {
				{ "/1.jpg", "Toothpaste", "30.00 ش.ج", "1", "Dental Care", "Low", "10", "A fresh minty toothpaste" },
				{ "/2.jpg", "Dental Floss", "15.00 ش.ج", "2", "Dental Care", "Medium", "15",
						"Floss for teeth cleaning" },
				{ "/3.png", "Electric Toothbrush", "300.00 ش.ج", "3", "Toothbrushes", "High", "20",
						"Electric toothbrush" },
				{ "/4.jpg", "Mouthwash", "50.00 ش.ج", "4", "Dental Care", "Low", "25",
						"Minty mouthwash for fresh breath" },
				{ "/5.png", "Tongue Scraper", "20.00 ش.ج", "5", "Dental Accessories", "High", "5",
						"Tongue scraper for better hygiene" },
				{ "/6.jpg", "Teeth Whitening Strips", "40.00 ش.ج", "6", "Dental Care", "Medium", "12",
						"Whitening strips for a brighter smile" } };

		HBox row = new HBox(40);
		row.getStyleClass().add("hbox");

		for (int i = 0; i < imageData.length; i++) {
			String productId = imageData[i][3];
			String productName = imageData[i][1];
			String productPrice = imageData[i][2];

			String category = imageData[i][4];
			String reorderLevel = imageData[i][5];
			String quantity = imageData[i][6];
			String description = imageData[i][7];

			Image image = new Image(getClass().getResourceAsStream(imageData[i][0]));
			ImageView imageView = new ImageView(image);
			imageView.setFitWidth(150);
			imageView.setFitHeight(150);
			imageView.setPreserveRatio(true);

			Label productNameLabel = new Label(productName);
			Label productPriceLabel = new Label(productPrice);
			productNameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
			productPriceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");

			Image plusIcon = new Image(getClass().getResourceAsStream("/icons8-plus-100.png"));
			ImageView plusIconView = new ImageView(plusIcon);
			plusIconView.setFitWidth(20);
			plusIconView.setFitHeight(20);
			Button plusButton = new Button();
			plusButton.setGraphic(plusIconView);
			plusButton.setStyle("-fx-background-color: transparent;");

			Image minusIcon = new Image(getClass().getResourceAsStream("/icons8-minus-100.png"));
			ImageView minusIconView = new ImageView(minusIcon);
			minusIconView.setFitWidth(20);
			minusIconView.setFitHeight(20);
			Button minusButton = new Button();
			minusButton.setGraphic(minusIconView);
			minusButton.setStyle("-fx-background-color: transparent;");

			VBox productDetails = new VBox(10, productNameLabel, productPriceLabel);
			productDetails.setStyle("-fx-alignment: center;");

			HBox buttonsBox = new HBox(10, plusButton, productDetails, minusButton);
			buttonsBox.setStyle("-fx-alignment: center;");

			VBox productBox = new VBox(10, imageView, buttonsBox);
			productBox.setStyle("-fx-alignment: center;");

			plusButton.setOnAction(e -> {
				Stage dialogStage = new Stage();
				dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/111.png")));

				dialogStage.setTitle("Enter Stock ID");

				Label stockIdLabel = new Label("Enter Stock ID:");
				stockIdLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

				TextField stockIdField = new TextField();
				stockIdField.setPromptText("Enter Stock ID");

				Button saveButton = new Button("Save");
				saveButton.getStyleClass().add("button-save");

				VBox dialogVBox = new VBox(10, stockIdLabel, stockIdField, saveButton);
				dialogVBox.setAlignment(Pos.CENTER_LEFT);
				dialogVBox.setPadding(new Insets(10));

				Scene scene = new Scene(dialogVBox, 280, 150);
				scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
				dialogStage.setScene(scene);
				dialogStage.show();

				saveButton.setOnAction(ev -> {
					String enteredStockId = stockIdField.getText().trim();

					if (enteredStockId.isEmpty()) {
						showAlert(Alert.AlertType.ERROR, "Error", "Stock ID cannot be empty.");
						return;
					}

					int stockId = 0;
					try {
						stockId = Integer.parseInt(enteredStockId);
					} catch (NumberFormatException ex) {
						showAlert(Alert.AlertType.ERROR, "Invalid Format",
								"Invalid Stock ID format: " + enteredStockId);
						return;
					}

					String checkStockQuery = "SELECT COUNT(*) FROM stock WHERE stockId = ?";
					String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/DBproject?useSSL=false";
					String username = "root";
					String password = "mysql";

					try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
							PreparedStatement checkStmt = conn.prepareStatement(checkStockQuery)) {
						checkStmt.setInt(1, stockId);

						ResultSet rs = checkStmt.executeQuery();
						if (rs.next() && rs.getInt(1) > 0) {
							double price = 0.0;
							try {
								price = Double.parseDouble(productPrice.replace(" ش.ج", ""));
							} catch (NumberFormatException ex) {
								showAlert(Alert.AlertType.ERROR, "Invalid Format",
										"Invalid price format: " + productPrice);
								return;
							}

							String insertQuery = "INSERT INTO product (productId, productName, category, reorderLevel, quantity, unitPrice, description, stockId) "
									+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
							try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
								stmt.setInt(1, Integer.parseInt(productId));
								stmt.setString(2, productName);
								stmt.setString(3, category);
								stmt.setInt(4, mapReorderLevel(reorderLevel));
								stmt.setInt(5, Integer.parseInt(quantity));
								stmt.setDouble(6, price);
								stmt.setString(7, description);
								stmt.setInt(8, stockId);

								int rowsAffected = stmt.executeUpdate();
								if (rowsAffected > 0) {
									showAlert(Alert.AlertType.INFORMATION, "Success",
											"Product " + productName + " added to database.");
									Product newProduct = new Product(Integer.parseInt(productId), productName, category,
											mapReorderLevel(reorderLevel), Integer.parseInt(quantity), price,
											description, stockId);

									productList.add(newProduct);
									productTableView.setItems(productList);
								} else {
									showAlert(Alert.AlertType.ERROR, "Failed", "Failed to add product to database.");
								}
							}
						} else {
							showAlert(Alert.AlertType.ERROR, "Invalid Stock ID",
									"The Stock ID does not exist in the stock table.");
						}
					} catch (SQLException ex) {
						showAlert(Alert.AlertType.ERROR, "Database Error",
								"Error during database operation: " + ex.getMessage());
					}

					dialogStage.close();
				});

			});

			minusButton.setOnAction(e -> {
				String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/DBproject?useSSL=false";
				String username = "root";
				String password = "mysql";

				String deleteQuery = "DELETE FROM product WHERE productId = ?";

				try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
					try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
						stmt.setInt(1, Integer.parseInt(productId));

						int rowsAffected = stmt.executeUpdate();
						if (rowsAffected > 0) {
							System.out.println("Product ID " + productId + " removed from database.");

							Product productToRemove = null;
							for (Product product : productList) {
								if (String.valueOf(product.getProductId()).equals(productId)) {
									productToRemove = product;
									break;
								}
							}

							if (productToRemove != null) {
								productList.remove(productToRemove);
								productTableView.setItems(productList);
								System.out.println("Product ID " + productId + " removed from table.");
							}
						} else {
							System.out.println("Product with ID " + productId + " not found in the database.");
						}
					}
				} catch (SQLException ex) {
					System.out.println("Error during database operation: " + ex.getMessage());
				}
			});

			row.getChildren().add(productBox);

			if ((i + 1) % 3 == 0 || i == imageData.length - 1) {
				vbox.getChildren().add(row);
				row = new HBox(40);
				row.getStyleClass().add("hbox");
			}
		}


		tabPane.getTabs().addAll(productTab,statisticsTab);

		productRoot.setCenter(tabPane);

		return productRoot;
	}

	private void showSearchAndEditDialog() {
		Stage searchEditStage = new Stage();
		searchEditStage.getIcons().add(new Image(getClass().getResourceAsStream("/88.png")));
		searchEditStage.setTitle("Search and Edit Product");
		searchEditStage.setMinWidth(400);
		searchEditStage.setMinHeight(400);

		VBox searchEditLayout = new VBox(10);
		searchEditLayout.setStyle("-fx-padding: 20; -fx-background-color: #15919B; -fx-alignment: center;");

		Label titleLabel = new Label("Search and Edit Product");
		titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

		TextField idField = new TextField();
		idField.setPromptText("Enter Product ID");
		idField.getStyleClass().add("input-field");

		Button searchButton = new Button("Search");
		searchButton.setStyle("-fx-background-color: linear-gradient(#C3C7F4, #15919B); " + "-fx-text-fill: white; "
				+ "-fx-font-weight: bold;");
		TextField productNameField = new TextField();
		productNameField.setPromptText("Product Name");
		productNameField.setDisable(true);

		TextField categoryField = new TextField();
		categoryField.setPromptText("Category");
		categoryField.setDisable(true);

		TextField reorderField = new TextField();
		reorderField.setPromptText("Reorder Level");
		reorderField.setDisable(true);

		TextField quantityField = new TextField();
		quantityField.setPromptText("Quantity");
		quantityField.setDisable(true);

		TextField unitPriceField = new TextField();
		unitPriceField.setPromptText("Unit Price");
		unitPriceField.setDisable(true);

		TextField descriptionField = new TextField();
		descriptionField.setPromptText("Description");
		descriptionField.setDisable(true);

		TextField stockIdField = new TextField();
		stockIdField.setPromptText("Stock ID");
		stockIdField.setDisable(true);

		Button saveButton = new Button("Save");
		saveButton.setStyle("-fx-background-color: linear-gradient(#C3C7F4, #15919B); " + "-fx-text-fill: white; "
				+ "-fx-font-weight: bold;");
		saveButton.setDisable(true);

		searchButton.setOnAction(ev -> {
			try {
				int id = Integer.parseInt(idField.getText());
				Connection con = db.getConnection().connectDB();
				String sql = "SELECT * FROM product WHERE productId=?";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, id);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					productNameField.setText(rs.getString("productName"));
					categoryField.setText(rs.getString("category"));
					reorderField.setText(String.valueOf(rs.getInt("reorderLevel")));
					quantityField.setText(String.valueOf(rs.getInt("quantity")));
					unitPriceField.setText(String.valueOf(rs.getDouble("unitPrice")));
					descriptionField.setText(rs.getString("description"));
					stockIdField.setText(String.valueOf(rs.getInt("stockId")));

					productNameField.setDisable(false);
					categoryField.setDisable(false);
					reorderField.setDisable(false);
					quantityField.setDisable(false);
					unitPriceField.setDisable(false);
					descriptionField.setDisable(false);
					stockIdField.setDisable(false);
					saveButton.setDisable(false);
				} else {
					showAlert(Alert.AlertType.WARNING, "Not Found", "Product with ID " + id + " was not found.");
				}

				rs.close();
				pstmt.close();
				con.close();
			} catch (NumberFormatException ex) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid numeric Product ID.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve product data.");
			}
		});

		saveButton.setOnAction(ev -> {
			try {
				int id = Integer.parseInt(idField.getText());
				String productName = productNameField.getText();
				String category = categoryField.getText();
				String description = descriptionField.getText();

				if (productName.isEmpty() || !productName.matches("[A-Za-z ]+")) {
					showAlert(Alert.AlertType.ERROR, "Input Error",
							"Product Name must contain only letters and cannot be empty.");
					return;
				}

				if (category.isEmpty() || !category.matches("[A-Za-z ]+")) {
					showAlert(Alert.AlertType.ERROR, "Input Error",
							"Category must contain only letters and cannot be empty.");
					return;
				}

				if (description.isEmpty() || !description.matches("[A-Za-z ]+")) {
					showAlert(Alert.AlertType.ERROR, "Input Error",
							"Description must contain only letters and cannot be empty.");
					return;
				}

				int reorderLevel = Integer.parseInt(reorderField.getText());
				int quantity = Integer.parseInt(quantityField.getText());
				double unitPrice = Double.parseDouble(unitPriceField.getText());
				int stockId = Integer.parseInt(stockIdField.getText());

				if (!isStockExist(stockId)) {
					showAlert(Alert.AlertType.ERROR, "Stock Not Found",
							"The stock with ID " + stockId + " does not exist.");
					return;
				}

				Connection con = db.getConnection().connectDB();
				String sql = "UPDATE product SET productName=?, category=?, reorderLevel=?, quantity=?, unitPrice=?, description=?, stockId=? WHERE productId=?";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setString(1, productName);
				pstmt.setString(2, category);
				pstmt.setInt(3, reorderLevel);
				pstmt.setInt(4, quantity);
				pstmt.setDouble(5, unitPrice);
				pstmt.setString(6, description);
				pstmt.setInt(7, stockId);
				pstmt.setInt(8, id);

				pstmt.executeUpdate();
				readProduct();
				showAlert(Alert.AlertType.INFORMATION, "Success", "Product details updated successfully.");
				searchEditStage.close();

				pstmt.close();
				con.close();
			} catch (NumberFormatException ex) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter valid numeric values.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to update product details.");
			}
		});

		searchEditLayout.getChildren().addAll(titleLabel, idField, searchButton, productNameField, categoryField,
				reorderField, quantityField, unitPriceField, descriptionField, stockIdField, saveButton);

		Scene searchEditScene = new Scene(searchEditLayout, 400, 450);
		searchEditScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		searchEditStage.setScene(searchEditScene);
		searchEditStage.show();
	}



	public VBox createAdvancedStatisticsLayout() {
	    VBox statisticsLayout = new VBox(10);
	    statisticsLayout.setPadding(new Insets(10));
	    statisticsLayout.setAlignment(Pos.CENTER_LEFT); 
	    statisticsLayout.setStyle("-fx-background-color: #15919B;");

	    Label titleLabel = new Label("Advanced Product Statistics:");
	    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

	    Label totalProductsLabel = new Label("Total Products: ");
	    Label totalQuantityLabel = new Label("Total Quantity: ");
	    Label averagePriceLabel = new Label("Average Price: ");
	    Label totalInventoryValueLabel = new Label("Total Inventory Value: ");
	    Label highestPricedProductLabel = new Label("Highest Priced Product: ");
	    Label lowestPricedProductLabel = new Label("Lowest Priced Product: ");
	    Label productsByCategoryLabel = new Label("Products by Category: ");
	    Label totalCategoriesLabel = new Label("Total Categories: ");
	    Label maxQuantityProductLabel = new Label("Product with Max Quantity: ");

	    Label[] labels = {
	            totalProductsLabel,
	            totalQuantityLabel,
	            averagePriceLabel,
	            totalInventoryValueLabel,
	            highestPricedProductLabel,
	            lowestPricedProductLabel,
	            productsByCategoryLabel,
	            totalCategoriesLabel,
	            maxQuantityProductLabel
	    };
	    for (Label label : labels) {
	        label.setStyle("-fx-text-fill: white;");
	    }

	    Button calculateStatisticsButton = new Button("Show Advanced Statistics");
	    calculateStatisticsButton.setStyle("-fx-background-color: linear-gradient(#C3C7F4, #15919B); -fx-text-fill: white; -fx-font-weight: bold;");

	    calculateStatisticsButton.setOnAction(e -> {
	        try (Connection con = db.getConnection().connectDB()) {
	            String sql = """
	                SELECT
	                    COUNT(*) AS total_products,
	                    SUM(quantity) AS total_quantity,
	                    AVG(unitPrice) AS average_price,
	                    (SELECT productName FROM product ORDER BY unitPrice DESC LIMIT 1) AS highest_priced_product,
	                    (SELECT unitPrice FROM product ORDER BY unitPrice DESC LIMIT 1) AS highest_price,
	                    (SELECT productName FROM product ORDER BY unitPrice ASC LIMIT 1) AS lowest_priced_product,
	                    (SELECT unitPrice FROM product ORDER BY unitPrice ASC LIMIT 1) AS lowest_price,
	                    SUM(unitPrice * quantity) AS total_inventory_value,
	                    COUNT(DISTINCT category) AS total_categories,
	                    (SELECT productName FROM product ORDER BY quantity DESC LIMIT 1) AS max_quantity_product
	                FROM product;
	                """;

	            String productsByCategorySql = """
	                SELECT category, COUNT(*) AS product_count FROM product GROUP BY category;
	                """;

	            PreparedStatement pstmt = con.prepareStatement(sql);
	            ResultSet rs = pstmt.executeQuery();

	            if (rs.next()) {
	                int totalProducts = rs.getInt("total_products");
	                int totalQuantity = rs.getInt("total_quantity");
	                double averagePrice = rs.getDouble("average_price");
	                String highestPricedProduct = rs.getString("highest_priced_product");
	                double highestPrice = rs.getDouble("highest_price");
	                String lowestPricedProduct = rs.getString("lowest_priced_product");
	                double lowestPrice = rs.getDouble("lowest_price");
	                double totalInventoryValue = rs.getDouble("total_inventory_value");
	                int totalCategories = rs.getInt("total_categories");
	                String maxQuantityProduct = rs.getString("max_quantity_product");

	                totalProductsLabel.setText("Total Products: " + totalProducts);
	                totalQuantityLabel.setText("Total Quantity: " + totalQuantity);
	                averagePriceLabel.setText("Average Price: " + String.format("%.2f", averagePrice) + " ش.ج");
	                highestPricedProductLabel.setText("Highest Priced Product: " + highestPricedProduct + " (" + highestPrice + " ش.ج)");
	                lowestPricedProductLabel.setText("Lowest Priced Product: " + lowestPricedProduct + " (" + lowestPrice + " ش.ج)");
	                totalInventoryValueLabel.setText("Total Inventory Value: " + String.format("%.2f", totalInventoryValue) + " ش.ج");
	                totalCategoriesLabel.setText("Total Categories: " + totalCategories);
	                maxQuantityProductLabel.setText("Product with Max Quantity: " + maxQuantityProduct);
	            }
	            rs.close();
	            pstmt.close();

	            pstmt = con.prepareStatement(productsByCategorySql);
	            rs = pstmt.executeQuery();
	            StringBuilder categoryStats = new StringBuilder();
	            while (rs.next()) {
	                categoryStats.append(rs.getString("category"))
	                             .append(": ")
	                             .append(rs.getInt("product_count"))
	                             .append(", ");
	            }
	            productsByCategoryLabel.setText("Products by Category: " + categoryStats.toString());
	            rs.close();
	            pstmt.close();
	        } catch (SQLException | ClassNotFoundException ex) {
	            ex.printStackTrace();
	            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve statistics.");
	        }
	    });

	    statisticsLayout.getChildren().addAll(
	            titleLabel,
	            totalProductsLabel,
	            totalQuantityLabel,
	            averagePriceLabel,
	            totalInventoryValueLabel,
	            highestPricedProductLabel,
	            lowestPricedProductLabel,
	            productsByCategoryLabel,
	            totalCategoriesLabel,
	            maxQuantityProductLabel,
	            calculateStatisticsButton
	    );

	    return statisticsLayout;
	}


	private int mapReorderLevel(String reorderLevel) {
		if (reorderLevel.equals("Low")) {
			return 1;
		} else if (reorderLevel.equals("Medium")) {
			return 2;
		} else if (reorderLevel.equals("High")) {
			return 3;
		}
		return 0;
	}


	private void searchProduct(int id) {
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT * FROM product WHERE productId=?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();

			ObservableList<Product> searchResultList = FXCollections.observableArrayList();

			if (rs.next()) {
				int productId = rs.getInt("productId");
				String productName = rs.getString("productName");
				String category = rs.getString("category");
				int reorderLevel = rs.getInt("reorderLevel");
				int quantity = rs.getInt("quantity");
				double unitPrice = rs.getDouble("unitPrice");
				String description = rs.getString("description");
				int stockId = rs.getInt("stockId");

				searchResultList.add(new Product(productId, productName, category, reorderLevel, quantity, unitPrice,
						description, stockId));
			}

			rs.close();
			pstmt.close();
			con.close();

			if (searchResultList.isEmpty()) {
				showAlert(Alert.AlertType.WARNING, "Product Not Found",
						"The product with ID " + id + " was not found.");
				return;
			}

			showSearchResultInTableView(searchResultList);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showSearchResultInTableView(ObservableList<Product> searchResultList) {
		Stage resultStage = new Stage();

		resultStage.getIcons().add(new Image(getClass().getResourceAsStream("/99.png")));

		resultStage.setTitle("Search Results");

		TableView<Product> resultTableView = new TableView<>();
		resultTableView.setItems(searchResultList);

		TableColumn<Product, Integer> idCol = new TableColumn<>("Product ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("productId"));
		idCol.setPrefWidth(100);

		TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
		nameCol.setPrefWidth(200);

		TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
		categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
		categoryCol.setPrefWidth(150);

		TableColumn<Product, Integer> reorderCol = new TableColumn<>("Reorder Level");
		reorderCol.setCellValueFactory(new PropertyValueFactory<>("reorderLevel"));
		reorderCol.setPrefWidth(100);

		TableColumn<Product, Integer> quantityCol = new TableColumn<>("Quantity");
		quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		quantityCol.setPrefWidth(100);

		TableColumn<Product, Double> priceCol = new TableColumn<>("Unit Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
		priceCol.setPrefWidth(100);

		TableColumn<Product, String> descriptionCol = new TableColumn<>("Description");
		descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
		descriptionCol.setPrefWidth(200);

		TableColumn<Product, Integer> stockIdCol = new TableColumn<>("Stock ID");
		stockIdCol.setCellValueFactory(new PropertyValueFactory<>("stockId"));
		stockIdCol.setPrefWidth(100);

		resultTableView.getColumns().addAll(idCol, nameCol, categoryCol, reorderCol, quantityCol, priceCol,
				descriptionCol, stockIdCol);

		VBox resultLayout = new VBox(10, resultTableView);
		resultLayout.setStyle("-fx-padding: 10;");

		Scene resultScene = new Scene(resultLayout, 1100, 400);
		resultScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

		resultStage.setScene(resultScene);
		resultStage.show();
	}

	private boolean isStockExist(int stockId) {
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT 1 FROM stock WHERE stockId = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, stockId);
			ResultSet rs = pstmt.executeQuery();
			boolean exists = rs.next();
			rs.close();
			pstmt.close();
			con.close();
			return exists;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void showAlert(Alert.AlertType alertType, String title, String content) {
		Stage alertStage = new Stage();
		alertStage.initModality(Modality.APPLICATION_MODAL);
		alertStage.setTitle(title);

		alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/11.png")));

		Label titleLabel = new Label(title);
		titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

		HBox titleBox = new HBox(10, titleLabel);
		titleBox.setAlignment(Pos.CENTER);

		Label messageLabel = new Label(content);
		messageLabel.setWrapText(true);
		messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

		Button closeButton = new Button("Close");
		closeButton.setStyle(
				"-fx-background-color: linear-gradient(#C3C7F4, #15919B); -fx-text-fill: white; -fx-font-weight: bold;");
		closeButton.setOnAction(e -> alertStage.close());

		VBox layout = new VBox(15, titleBox, messageLabel, closeButton);
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.CENTER);
		layout.setStyle("-fx-background-color: linear-gradient(#15919B, #C3C7F4); -fx-border-radius: 10;");

		Scene scene = new Scene(layout, 400, 200);
		alertStage.setScene(scene);
		alertStage.showAndWait();
	}
}