package libreria.test;

import static java.time.Duration.ofSeconds;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

public class LibreriaTest {

	@Test
	public void guardarTest() {
		WebDriver driver = new ChromeDriver();
		try {
			//ABRE UN TAB DEL NAVEGADOR Y ENTRA A LA PANTALLA INDICADA
			driver.get("http://localhost:8080/");
			
			//ESPERA A QUE CARGUE LA PANTALLA
			new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
				.until(titleIs("Libros de la Biblioteca"));
			
			//UBICA UN CAMBPO DE TEXTO A LLENAR
			WebElement camponombre = driver.findElement(By.xpath("//vaadin-text-field[@id='txtNombreLibro']/input"));
			
			//CLICK EN EL CAMPO DE TEXTO Y ESCRIBE EN EL
			camponombre.click();
			camponombre.sendKeys("Susana Andrea Perez");
			Thread.sleep(2000);
			
			//SELECCIONANDO BOTON
			WebElement botonGuardar = driver.findElement(By.xpath("//vaadin-button[@id='btnGuardar']"));
			//CLICK EN EL BOTÓN GUARDAR
			botonGuardar.click();
			
			
			//ESPERA UN TIEMPO PARA QUE PODAMOS VER LO QUE HIZO
			Thread.sleep(10000);
			
		}catch(Exception error) {
			error.printStackTrace();
		}finally {
			//CIERRA EL TAB DEL NAGEVADOR
			driver.close();
		}
		
		
	}
}
