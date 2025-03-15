<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Produktliste</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }
        h1 {
            color: #333;
            border-bottom: 1px solid #eee;
            padding-bottom: 10px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        th, td {
            padding: 10px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f4f4f4;
        }
        tr:hover {
            background-color: #f9f9f9;
        }
        .back-link {
            display: inline-block;
            margin-top: 20px;
            padding: 10px;
            background-color: #f4f4f4;
            border-radius: 4px;
            text-decoration: none;
            color: #333;
        }
        .back-link:hover {
            background-color: #e4e4e4;
        }
    </style>
</head>
<body>
    <h1>Produktliste</h1>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Beschreibung</th>
                <th>Preis (€)</th>
                <th>Lagerbestand</th>
            </tr>
        </thead>
        <tbody>
            <#list products as product>
                <tr>
                    <td>${product.id}</td>
                    <td>${product.name}</td>
                    <td>${product.description}</td>
                    <td>${product.price?string("0.00")}</td>
                    <td>${product.stock}</td>
                </tr>
            </#list>
        </tbody>
    </table>

    <a href="/" class="back-link">Zurück zur Startseite</a>
</body>
</html>