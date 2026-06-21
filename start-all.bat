@echo off
set ROOT=C:\Users\vayal\OneDrive\Documentos\GitHub\Biblioteca_Lagos_

start "libros-8081" cmd /c "cd /d "%ROOT%\libros" && mvnw.cmd spring-boot:run"
start "categorias-8082" cmd /c "cd /d "%ROOT%\Categorias" && mvnw.cmd spring-boot:run"
start "proveedores-8083" cmd /c "cd /d "%ROOT%\Proveedores" && mvnw.cmd spring-boot:run"
start "prestamos-8084" cmd /c "cd /d "%ROOT%\Prestamos" && mvnw.cmd spring-boot:run"
start "socios-8085" cmd /c "cd /d "%ROOT%\Socios" && mvnw.cmd spring-boot:run"
start "tiposocio-8086" cmd /c "cd /d "%ROOT%\tipoSocios" && mvnw.cmd spring-boot:run"
start "multas-8087" cmd /c "cd /d "%ROOT%\Multas" && mvnw.cmd spring-boot:run"
start "roles-8088" cmd /c "cd /d "%ROOT%\Roles" && mvnw.cmd spring-boot:run"
start "reservas-8090" cmd /c "cd /d "%ROOT%\Reservas" && mvnw.cmd spring-boot:run"
start "usuarios-8095" cmd /c "cd /d "%ROOT%\Usuarios" && mvnw.cmd spring-boot:run"
