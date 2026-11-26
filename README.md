<div align="center">

# SBardamage

### El daño que haces, en tu actionbar, en tiempo real.

Feedback de combate limpio y ligero, sin ensuciar el chat.

[![build](https://github.com/JhonTova/SBarDamage/actions/workflows/build.yml/badge.svg)](https://github.com/JhonTova/SBarDamage/actions/workflows/build.yml) [![release](https://img.shields.io/github/v/release/JhonTova/SBarDamage?label=release)](https://github.com/JhonTova/SBarDamage/releases/latest) ![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11%2B-62B47A) ![Java](https://img.shields.io/badge/Java-21-E76F00)

</div>

---

## Características

- Muestra el **daño infligido** directamente en la **actionbar**.
- **Seguimiento de hasta 3 objetivos** a la vez.
- Ligero: sin GUIs, sin base de datos, cero peso sobre el servidor.
- Formato y colores personalizables con MiniMessage (vía SiriusCore).

## Requisitos

Requiere **[SiriusCore](https://github.com/JhonTova/SiriusCore)**.

## Instalación

`SiriusCore.jar` + `SBardamage.jar` en `plugins/`. Spigot / Paper / Purpur 1.21.11+.

## Descarga

El `.jar` de cada versión está en [Releases](https://github.com/JhonTova/SBarDamage/releases/latest).

## Compilar desde el código

Requiere Java 21 y Maven. SiriusCore no está en un repositorio Maven público, así que
primero se instala en local la versión que declara el `pom.xml`:

```bash
git clone --branch v1.1.1 https://github.com/JhonTova/SiriusCore.git
mvn -f SiriusCore/pom.xml install -DskipTests
mvn package        # en este repositorio; el .jar queda en target/
```

Cada push y pull request se compila en GitHub Actions. Al subir una tag `v*`, el `.jar`
se adjunta solo a la release.

## Licencia

[MIT](LICENSE): puedes usarlo, modificarlo y redistribuirlo manteniendo el aviso de copyright.
Requiere SiriusCore, que tiene su propia licencia.
