# InterviewTask

When you clone the repo on your local PC, you can run it through the `Trading212TaskApplication` class like any other Spring Boot app. All the dependencies are in the `pom.xml` file, and there should be no issues running the app.

Also, the reason there is a `ServletInitializer` class in the project is because I accidentally created the project as a WAR and later changed it to a JAR. All the files responsible for the frontend logic are in the `resources` folder, specifically within the `static` (for CSS and other static assets) and `templates` folders.

When you run the project for the first time, make sure to uncomment the `spring.sql.init.mode=always` property. This will run the `schema.sql` file and create the database that the project needs in order to function correctly.