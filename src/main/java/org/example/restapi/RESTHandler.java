package org.example.restapi;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.OpenApiPluginConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import org.example.restapi.database.DatabaseHandler;
import org.example.restapi.models.Product;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class RESTHandler {
    private DatabaseHandler dbHandler;
    private Javalin app;
    private Properties properties;

    public static void main(String[] args) throws IOException {
        new RESTHandler(new DatabaseHandler());
    }

    public RESTHandler(DatabaseHandler dbHandler) throws IOException {
        this.dbHandler = dbHandler;

        try (FileInputStream input = new FileInputStream("src/main/resources/config.properties")) {
            properties = new Properties();
            properties.load(input);
        } catch (FileNotFoundException e) {
            System.out.println("Konfigurationsdatei nicht gefunden. Standard-Port 8080 wird verwendet.");
            properties = new Properties();
            properties.setProperty("server.port", "8080");
        }

        int localPort = Integer.parseInt(properties.getProperty("server.port", "8080"));
        start(localPort);
    }

    private void start(int port) {
        // Javalin-App erstellen und konfigurieren
        app = Javalin.create(config -> {
            // OpenAPI-Plugin konfigurieren
            OpenApiPluginConfiguration openApiConfig = new OpenApiPluginConfiguration()
                    .withDocumentationPath("/swagger-docs")
                    .withDefinitionConfiguration((version, definition) -> {
                        definition.withOpenApiInfo((openApiInfo) -> {
                            openApiInfo.setTitle("Produkt-API");
                            openApiInfo.setVersion("1.0");
                            openApiInfo.setDescription("RESTful API für die Verwaltung von Produkten");
                        });
                    });

            // OpenAPI und Swagger Plugins registrieren
            config.plugins.register(new OpenApiPlugin(openApiConfig));
            SwaggerConfiguration swaggerConfig = new SwaggerConfiguration();
            swaggerConfig.setDocumentationPath("/swagger-docs"); // 必须与OpenApiPluginConfiguration中的路径匹配
            swaggerConfig.setUiPath("/swagger-ui"); // 定义Swagger UI的访问路径
            swaggerConfig.setTitle("Produkt-API");
            config.plugins.register(new SwaggerPlugin(swaggerConfig));

            // Statische Dateien konfigurieren
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.directory = "./src/main/resources/static";
                staticFileConfig.location = Location.EXTERNAL;
            });
        }).start(port);

        System.out.println("Server gestartet auf http://localhost:" + port);

        // Routen definieren
        defineRoutes();
    }

    private void defineRoutes() {
        // Startseite
        app.get("/", ctx -> {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head><title>Produkt-API</title>");
            html.append("<style>body{font-family:Arial;max-width:800px;margin:0 auto;padding:20px;}");
            html.append("h1{color:#333;border-bottom:1px solid #eee;padding-bottom:10px;}");
            html.append(".api-link{display:block;margin:10px 0;padding:10px;background-color:#f4f4f4;");
            html.append("border-radius:4px;text-decoration:none;color:#333;}");
            html.append(".api-link:hover{background-color:#e4e4e4;}</style></head><body>");
            html.append("<h1>Willkommen zur Produkt-API</h1>");
            html.append("<p>Dies ist eine einfache RESTful API zur Verwaltung von Produkten.</p>");

            html.append("<h2>Verfügbare Endpunkte</h2>");
            html.append("<ul>");
            html.append("<li>GET /api/products - Alle Produkte abrufen</li>");
            html.append("<li>GET /api/products/{id} - Produkt nach ID abrufen</li>");
            html.append("<li>POST /api/products - Neues Produkt erstellen</li>");
            html.append("<li>PUT /api/products/{id} - Produkt aktualisieren</li>");
            html.append("<li>DELETE /api/products/{id} - Produkt löschen</li>");
            html.append("</ul>");

            html.append("<h2>Produkte anzeigen</h2>");
            html.append("<a href='/products/view' class='api-link'>Produkte anzeigen</a>");

            html.append("<h2>API-Dokumentation</h2>");
            html.append("<a href='/swagger' class='api-link'>Swagger UI öffnen</a>");

            html.append("</body></html>");

            ctx.html(html.toString());
        });

        // API-Routen
        getAllProducts();
        getProductById();
        createProduct();
        updateProduct();
        deleteProduct();
        productListView();
    }

    @OpenApi(
            path = "/api/products",
            methods = {HttpMethod.GET},
            summary = "Alle Produkte abrufen",
            tags = {"Produkte"},
            responses = {
                    @OpenApiResponse(status = "200", description = "Eine Liste aller Produkte", content = {@OpenApiContent(from = Product[].class)})
            }
    )
    private void getAllProducts() {
        app.get("/api/products", ctx -> {
            ctx.json(dbHandler.getAllProducts());
        });
    }

    @OpenApi(
            path = "/api/products/{id}",
            methods = {HttpMethod.GET},
            summary = "Produkt nach ID abrufen",
            description = "Hallloooo",
            tags = {"Produkte"},
            pathParams = {
                    @OpenApiParam(name = "id", description = "Die ID des Produkts")
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Das Produkt mit der angegebenen ID", content = {@OpenApiContent(from = Product.class)}),
                    @OpenApiResponse(status = "404", description = "Produkt nicht gefunden")
            }
    )
    private void getProductById() {
        app.get("/api/products/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Product product = dbHandler.getProductById(id);
            if (product != null) {
                ctx.json(product);
            } else {
                ctx.status(404).result("Produkt nicht gefunden");
            }
        });
    }

    @OpenApi(
            path = "/api/products",
            methods = {HttpMethod.POST},
            summary = "Neues Produkt erstellen",
            description = "Hallloooo",
            tags = {"Produkte"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Product.class)}),
            responses = {
                    @OpenApiResponse(status = "201", description = "Produkt hinzugefügt")
            }
    )
    private void createProduct() {
        app.post("/api/products", ctx -> {
            Product newProduct = ctx.bodyAsClass(Product.class);
            dbHandler.insertProduct(newProduct);
            ctx.status(201).result("Produkt hinzugefügt");
        });
    }

    @OpenApi(
            path = "/api/products/{id}",
            methods = {HttpMethod.PUT},
            summary = "Produkt aktualisieren",
            description = "Hallloooo",
            tags = {"Produkte"},
            pathParams = {
                    @OpenApiParam(name = "id", description = "Die ID des zu aktualisierenden Produkts")
            },
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Product.class)}),
            responses = {
                    @OpenApiResponse(status = "200", description = "Produkt aktualisiert"),
                    @OpenApiResponse(status = "404", description = "Produkt nicht gefunden")
            }
    )
    private void updateProduct() {
        app.put("/api/products/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Product updatedProduct = ctx.bodyAsClass(Product.class);
            updatedProduct.setId(id);
            boolean updated = dbHandler.updateProduct(updatedProduct);
            if (updated) {
                ctx.status(200).result("Produkt aktualisiert");
            } else {
                ctx.status(404).result("Produkt nicht gefunden");
            }
        });
    }

    @OpenApi(
            path = "/api/products/{id}",
            methods = {HttpMethod.DELETE},
            summary = "Produkt löschen",
            description = "Hallloooo",
            tags = {"Produkte"},
            pathParams = {
                    @OpenApiParam(name = "id", description = "Die ID des zu löschenden Produkts")
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Produkt gelöscht"),
                    @OpenApiResponse(status = "404", description = "Produkt nicht gefunden")
            }
    )
    private void deleteProduct() {
        app.delete("/api/products/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean deleted = dbHandler.deleteProduct(id);
            if (deleted) {
                ctx.status(200).result("Produkt gelöscht");
            } else {
                ctx.status(404).result("Produkt nicht gefunden");
            }
        });
    }

    // HTML-Ansicht für Produktliste (keine OpenAPI-Dokumentation nötig)
    private void productListView() {
        app.get("/products/view", ctx -> {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head><title>Produktliste</title>");
            html.append("<style>body{font-family:Arial;max-width:800px;margin:0 auto;padding:20px;}");
            html.append("table{width:100%;border-collapse:collapse;}");
            html.append("th,td{padding:10px;text-align:left;border-bottom:1px solid #ddd;}");
            html.append("th{background-color:#f4f4f4;}</style></head><body>");
            html.append("<h1>Produktliste</h1><table><tr><th>ID</th><th>Name</th><th>Beschreibung</th><th>Preis</th><th>Lagerbestand</th></tr>");

            for (Product product : dbHandler.getAllProducts()) {
                html.append("<tr>");
                html.append("<td>").append(product.getId()).append("</td>");
                html.append("<td>").append(product.getName()).append("</td>");
                html.append("<td>").append(product.getDescription()).append("</td>");
                html.append("<td>").append(product.getPrice()).append(" €</td>");
                html.append("<td>").append(product.getStock()).append("</td>");
                html.append("</tr>");
            }

            html.append("</table>");
            html.append("<a href='/'>Zurück zur Startseite</a>");
            html.append("</body></html>");

            ctx.html(html.toString());
        });
    }
}