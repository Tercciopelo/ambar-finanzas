# Ámbar Finanzas

Aplicación Android de finanzas personales creada para llevar ingresos, gastos, pagos pendientes y compras en cuotas de forma simple. Funciona completamente offline: no usa Firebase, cuentas, analítica ni servicios externos.

## Funciones principales

- Inicio con balance mensual, pagos pendientes y referencia diaria.
- Registro rápido de gastos e ingresos, con fecha, categoría y nota opcionales.
- Gastos habituales reutilizables, sin crear cobros automáticos.
- Compras en cuotas con saldo restante y registro de cada pago como gasto.
- Búsqueda y filtros de movimientos por mes y estado.
- Tema claro, oscuro o del sistema, y modo privado para ocultar montos.
- Respaldo y restauración en JSON, además de exportación CSV.

## Privacidad y arquitectura

- Datos guardados únicamente en Room SQLite en el dispositivo.
- Interfaz Jetpack Compose con Material 3.
- MVVM con `ViewModel`, `StateFlow` y `FinanceRepository`.

## Compilación y pruebas

Este repositorio se valida mediante GitHub Actions. Cada envío a `main` ejecuta las pruebas unitarias, compila el APK de depuración y publica el artefacto `Ambar-Finanzas-APK` en la ejecución correspondiente.

El workflow de versión firmada se ejecuta manualmente o al crear una etiqueta `v*`, siempre que los secretos de firma estén configurados en GitHub.

## Respaldo

En **Ajustes > Tus datos** se puede guardar un respaldo JSON, restaurar uno existente o exportar los movimientos a CSV. Restaurar reemplaza el contenido local actual, por lo que la aplicación solicita confirmación antes de elegir el archivo.
