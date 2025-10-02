import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner lector = new Scanner(System.in);

        System.out.println("=== Metodo Simplex en Java ===");
        System.out.print("¿Desea Maximizar o Minimizar? (max/min): ");
        String tipo = lector.next().toLowerCase();

        System.out.print("Ingrese el número de variables de decisión: ");
        int n = lector.nextInt();

        System.out.print("Ingrese el número de restricciones: ");
        int m = lector.nextInt();

        // Creamos la tabla simplex
        double[][] tabla = new double[m + 1][n + m + 1];

        // Coeficientes de la función objetivo
        System.out.println("\nIngrese los coeficientes de la función objetivo:");
        for (int j = 0; j < n; j++) {
            System.out.print("Coeficiente de x" + (j + 1) + ": ");
            double val = lector.nextDouble();
            tabla[m][j] = (tipo.equals("max")) ? -val : val; // en max se meten negativos
        }

        // Restricciones
        for (int i = 0; i < m; i++) {
            System.out.println("\nRestricción " + (i + 1));
            for (int j = 0; j < n; j++) {
                System.out.print("Coeficiente de x" + (j + 1) + ": ");
                tabla[i][j] = lector.nextDouble();
            }
            // variable de holgura
            tabla[i][n + i] = 1;
            System.out.print("Valor del lado derecho (b): ");
            tabla[i][n + m] = lector.nextDouble();
        }

        simplex(tabla, m, n, tipo.equals("max"));
    }

    // Metodo simplex
    static void simplex(double[][] tabla, int m, int n, boolean maximizar) {
        while (true) {
            int col = columnaPivote(tabla, m, n + m);
            if (col == -1) break; // ya es óptima

            int fila = filaPivote(tabla, m, n + m, col);
            if (fila == -1) {
                System.out.println("Solución ilimitada.");
                return;
            }

            // Mostrar tabla antes del pivoteo
            mostrarTabla(tabla, m, n + m);

            // Hacemos el pivoteo
            pivoteo(tabla, m, n + m, fila, col);

            System.out.println("Pivoteando en fila " + fila + " y columna " + col);
        }

        // Mostrar la tabla final
        mostrarTabla(tabla, m, n + m);

        // Resultado
        System.out.println("\n--- Solución Óptima ---");
        double[] sol = new double[n];
        for (int j = 0; j < n; j++) {
            int fila = variableBasica(tabla, m, j);
            if (fila != -1) sol[j] = tabla[fila][n + m];
        }
        for (int j = 0; j < n; j++) {
            System.out.printf("x%d = %.2f\n", (j + 1), sol[j]);
        }
        double z = tabla[m][n + m];
        if (maximizar) z = -z; // corregir signo
        System.out.printf("Valor óptimo de Z = %.2f\n", z);
    }

    // Buscar la columna pivote
    static int columnaPivote(double[][] tabla, int m, int totalCols) {
        int col = -1;
        double min = 0;
        for (int j = 0; j < totalCols; j++) {
            if (tabla[m][j] < min) {
                min = tabla[m][j];
                col = j;
            }
        }
        return col;
    }

    // Buscar la fila pivote (mínimo cociente positivo)
    static int filaPivote(double[][] tabla, int m, int totalCols, int col) {
        int fila = -1;
        double minRatio = Double.MAX_VALUE;
        for (int i = 0; i < m; i++) {
            if (tabla[i][col] > 0) {
                double ratio = tabla[i][totalCols] / tabla[i][col];
                if (ratio < minRatio) {
                    minRatio = ratio;
                    fila = i;
                }
            }
        }
        return fila;
    }

    // Pivoteo
    static void pivoteo(double[][] tabla, int m, int totalCols, int fila, int col) {
        double pivote = tabla[fila][col];
        for (int j = 0; j <= totalCols; j++) {
            tabla[fila][j] /= pivote;
        }
        for (int i = 0; i <= m; i++) {
            if (i != fila) {
                double factor = tabla[i][col];
                for (int j = 0; j <= totalCols; j++) {
                    tabla[i][j] -= factor * tabla[fila][j];
                }
            }
        }
    }

    // Imprimir la tabla simplex
    static void mostrarTabla(double[][] tabla, int m, int totalCols) {
        System.out.println("\nTabla Simplex:");
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= totalCols; j++) {
                System.out.printf("%8.2f ", tabla[i][j]);
            }
            System.out.println();
        }
        System.out.println("---------------------------");
    }

    // Ver si una variable es básica
    static int variableBasica(double[][] tabla, int m, int col) {
        int fila = -1;
        for (int i = 0; i < m; i++) {
            if (tabla[i][col] == 1) {
                if (fila == -1) fila = i;
                else return -1;
            } else if (tabla[i][col] != 0) {
                return -1;
            }
        }
        return fila;

    }
}