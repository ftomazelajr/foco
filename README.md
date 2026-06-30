# Foco — app de auto-bloqueio

App simples de auto-controle: você escolhe qual app bloquear (ex: `sg.bigo.live` pro Bigo Live) e por quantos dias. Tudo é transparente — nada escondido, você sempre pode ver e, com uma etapa de confirmação proposital, encerrar antes do prazo.

## Como gerar o APK

Eu não tenho Android SDK neste ambiente pra compilar o APK final assinado, mas o projeto está pronto pra buildar de duas formas:

### Opção 1 — GitHub Actions (igual ao seu fluxo atual)
1. Crie um repositório novo no GitHub e suba esta pasta inteira.
2. O workflow em `.github/workflows/build.yml` já compila automaticamente a cada push na `main`.
3. Baixe o APK gerado na aba **Actions > build > Artifacts**.

### Opção 2 — Android Studio
1. Abra a pasta `Foco` no Android Studio.
2. Deixe ele sincronizar o Gradle.
3. `Build > Build Bundle(s)/APK(s) > Build APK(s)`.

## Como usar
1. Instale o APK no seu celular.
2. Abra o app, toque em "Ativar serviço de acessibilidade" e habilite o "Foco" nas configurações.
3. Volte ao app, digite o pacote (`sg.bigo.live`) e os dias, e toque em "Iniciar bloqueio".
4. Pronto — sempre que o Bigo Live abrir, ele fecha sozinho e volta pra tela inicial.

## Sobre a fricção de cancelamento
Pra encerrar antes do prazo, o app pede pra digitar a frase "quero desistir". Isso é só um momento de reflexão — você sempre tem controle total do seu próprio celular, pode desinstalar o app normalmente a qualquer momento (ele não usa Device Admin nem se esconde).
