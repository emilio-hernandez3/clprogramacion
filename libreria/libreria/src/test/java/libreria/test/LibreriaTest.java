package libreria.test;

import static java.time.Duration.ofSeconds;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
			WebElement campoautor = driver.findElement(By.xpath("//vaadin-text-field[@id='txtAutorLibro']/input"));
			WebElement campoeditorial = driver.findElement(By.xpath("//vaadin-text-field[@id='txtEditorial']/input"));
			WebElement campocantidadPaginas = driver.findElement(By.xpath("//vaadin-text-field[@id='txtCantidadPaginas']/input"));
			WebElement campoisbn = driver.findElement(By.xpath("//vaadin-text-field[@id='txtIsbn']/input"));
			
			//CLICK EN EL CAMPO DE TEXTO Y ESCRIBE EN EL
			camponombre.click();
			camponombre.sendKeys("Programación Avanzada II en Java");
			
			campoautor.click();
			campoautor.sendKeys("Susana Andrea Perez");
			
			campoeditorial.click();
			campoeditorial.sendKeys("Editorial Thompson");
			
			campocantidadPaginas.click();
			campocantidadPaginas.sendKeys("350");
			
			campoisbn.click();
			campoisbn.sendKeys("12341249839793");
			
			WebElement campoFechaPublicacion = driver.findElement(By.xpath("//vaadin-date-picker[@id='publicationDatePicker']/input"));
			campoFechaPublicacion.click();
			
			WebElement campoToday = driver.findElement(By.xpath("//vaadin-button[contains(.,'Today')]"));
			campoToday.click();
			
			//SELECCIONANDO BOTON
			WebElement botonGuardar = driver.findElement(By.xpath("//vaadin-button[@id='btnGuardar']"));
			//CLICK EN EL BOTÓN GUARDAR
			botonGuardar.click();
			
			//xpath=//vaadin-button[@id='btnCancelar']
			
			//tiempo de espera para poder nosotros ver lo que hace la prueba (porque es muy rápida)
			//Thread.sleep(2000);
			String mensajeObtenido = "";
			try {
				WebElement mensaje = driver.findElement(By.xpath("//vaadin-notification-card[contains(.,'Data updated')]"));
				
				System.out.println("Mensaje Popup: "+mensaje.getText());
				mensajeObtenido = mensaje.getText();
				
				//assertNotNull(mensaje);
			
			}catch(org.openqa.selenium.NoSuchElementException e) {
				e.printStackTrace();
			}
			assertEquals(mensajeObtenido, "Data updated");
			
			//ESPERA UN TIEMPO PARA QUE PODAMOS VER LO QUE HIZO
			//Thread.sleep(5000);
			
		}catch(Exception error) {
			error.printStackTrace();
		}finally {
			//CIERRA EL TAB DEL NAGEVADOR
			driver.close();
		}
		
		
	}
}
