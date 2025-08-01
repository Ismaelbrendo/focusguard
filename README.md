# 📵 FocusGuard - Mantenha o Foco. Controle seu Tempo.

FocusGuard é um aplicativo Android moderno e leve, desenvolvido em **Java**, que te ajuda a **monitorar e bloquear aplicativos** que distraem, melhorando sua produtividade e bem-estar digital.

![FocusGuard Banner](docs/banner.png) <!-- Substituir com uma imagem real no futuro -->

---

## ✨ Funcionalidades

- 📊 **Dashboard de uso** com estatísticas de tempo de tela em tempo real.
- ⛔ **Sistema de bloqueio inteligente** de apps com interface amigável.
- 🔒 **Permissões avançadas** tratadas automaticamente no onboarding.
- ⚙️ **Serviço de acessibilidade eficiente**, detecta quando um app bloqueado é aberto e o bloqueia instantaneamente.
- 💾 **Persistência local segura** usando SharedPreferences.

---

## 🚀 Instalação

### 1. Clone o projeto

```bash
git clone https://github.com/seu-usuario/focusguard.git
cd focusguard
```

### 2. Abra no Android Studio

- Vá em **File > Open** e selecione a pasta do projeto.
- Aguarde a sincronização do Gradle.

### 3. Compile e execute

- Conecte um dispositivo físico ou use um emulador.
- Pressione **Run ▶️** no Android Studio.
- Ao iniciar, siga o processo de onboarding para conceder permissões.

> **Obs:** Você também pode gerar o `.apk` diretamente via `Build > Build Bundle(s) / APK(s) > Build APK`.

---

## 📱 Capturas de Tela

| Dashboard 📊 | Tela de Bloqueio 🔒 | Lista de Apps 🚫 |
|--------------|---------------------|------------------|
| ![Dashboard](docs/dashboard.png) | ![Bloqueio](docs/blocking.png) | ![Lista](docs/list.png) |

---

## 🧠 Como Funciona

O FocusGuard opera com base em três pilares principais:

1. **Permissões** — O usuário concede permissões de uso, sobreposição e acessibilidade.
2. **Monitoramento** — O app coleta o uso de apps com o `UsageStatsManager`.
3. **Bloqueio** — O `AccessibilityService` detecta apps em primeiro plano e bloqueia, se necessário, lançando uma `BlockingActivity`.

---

## 📁 Estrutura do Projeto

```
FocusGuard/
├── java/com/example/focusguard/
│   ├── activities/
│   │   ├── OnboardingActivity.java
│   │   ├── MainActivity.java
│   │   └── BlockingActivity.java
│   ├── fragments/
│   │   ├── DashboardFragment.java
│   │   └── BlockingFragment.java
│   ├── adapters/
│   │   ├── UsageStatsAdapter.java
│   │   └── BlockingAdapter.java
│   ├── models/
│   │   ├── AppUsageInfo.java
│   │   └── AppInfo.java
│   ├── services/
│   │   └── Accessibility.java
│   └── utils/
│       └── PermissionChecker.java
├── res/layout/
├── res/xml/
│   └── accessibility_service_config.xml
└── AndroidManifest.xml
```

---

## 🔐 Permissões Necessárias

- `PACKAGE_USAGE_STATS`
- `SYSTEM_ALERT_WINDOW`
- `BIND_ACCESSIBILITY_SERVICE`
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`

---

## 🛠️ Tecnologias

- Java (Android)
- Android SDK
- RecyclerView
- AccessibilityService
- SharedPreferences

---

## 📦 Status do Projeto

✅ **Pronto para uso e testes.**

- O aplicativo já pode ser instalado e utilizado via `.apk`.
- O serviço de acessibilidade está integrado e funcional.
- Permissões são tratadas automaticamente.
- Nenhuma dependência externa adicional é necessária.

---

## 📄 Licença

[MIT](LICENSE)

---

## 💬 Contato

Desenvolvido por **Seu Nome** — [@seu_usuario](https://github.com/seu_usuario)

---