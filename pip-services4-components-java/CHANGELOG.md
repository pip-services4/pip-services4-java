# <img src="https://uploads-ssl.webflow.com/5ea5d3315186cf5ec60c3ee4/5edf1c94ce4c859f2b188094_logo.svg" alt="Pip.Services Logo" width="200"> <br/> Portable Component Model for Java ChangeLog

## <a name="0.0.5"></a>Pip.Services 4 0.0.5 (2024-05-27)
Fixed Factory.create() method to produce correct results when comparing registration locators.

## <a name="0.0.4"></a>Pip.Services 4 0.0.4 (2024-05-27)
Fixed Factory.canCreate() method to produce correct results when comparing registration locators.

## <a name="0.0.1"></a>Pip.Services 4 0.0.1 (2023-05-24)
Moved code from commons module in PipService 3

### New Features:
* Added **IContext** interface and default **Context** implementation

### Breaking Changes:
* **correlationId** was replaced by **context**
* Moved **ContextInfo** and **DefaultContextFactory** under **context** package