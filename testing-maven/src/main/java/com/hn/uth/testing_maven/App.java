package com.hn.uth.testing_maven;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ){
        System.out.println( "Bienvenido a mi calculadora");
        double num1 = 7;
        double num2 = 3;
        double resultado = sumar(num1, num2);
        double resultadoResta = restar(num1, num2);
        
        System.out.println("La suma de "+num1+" + "+num2+" es "+resultado);
        System.out.println("La resta de "+num1+" - "+num2+" es "+resultadoResta);
    }

	public static double sumar(double primerNumero, double segundoNumero) {
		return primerNumero+segundoNumero;
	}

	public static double restar(double primerNumero, double segundoNumero) {
		return primerNumero-segundoNumero;
	}

	public static double multiplicar(double multiplicando, double multiplicador) {
		return multiplicando*multiplicador;
	}

	public static Double dividir(double dividendo, double divisor) {
		Double respuesta = null;
		if(divisor != 0) {
			respuesta = dividendo/divisor;
		}
		
		return respuesta;
	}
}
