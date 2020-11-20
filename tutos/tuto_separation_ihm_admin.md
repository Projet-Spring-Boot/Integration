# Séparer en deux micro-services différents (IHM & Admin) un microservice existant

## État actuel
Actuellement, voilà comment marche le micro-services :

Formulaire de la page signupPage.html :
```html
<form th:object="${myForm}" th:action="@{/signup}" method="POST">...</form>
```

`th:object` permet d'enregistrer les résultats du formulaire dans un object. Cet object est défni dans l'endpoint qui permet d'afficher la page :

```java
	@RequestMapping(value = { "/signup" }, method = RequestMethod.GET)
	public String signupPage(WebRequest request, Model model) {

		ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils(connectionFactoryLocator, connectionRepository);

		// Retrieve social networking information.
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
		
		AppUserForm myForm = null;

		if (connection != null) {
			myForm = new AppUserForm(connection);

			System.out.println("provider = " + myForm.getSignInProvider());
		} else {
			myForm = new AppUserForm();
		}

		// Envoie au formulaire HTML un objet du type AppUserForm
		model.addAttribute("myForm", myForm);
		return "signupPage";
	}
```

Lorsque l'utilisateur a complété le formulaire et appui sur "submit", cela à pour effet d'envoyer une requête *POST* vers `/signup` (Cf. form du code HTML ci-dessus).

L'API possède donc un endpoint `/signup` en *POST* permettant de rajouter l'utilisateur en BDD (+ créer un enregistrement dans la BDD 'UserConnection' si l'utilisateur se connecte via un réseau social).

```java
	@RequestMapping(value = { "/signup" }, method = RequestMethod.POST)
	public String signupSave(WebRequest request, Model model,
			@ModelAttribute("myForm") @Validated AppUserForm appUserForm, BindingResult result,
			final RedirectAttributes redirectAttributes) {

		// Validation error.
		if (result.hasErrors()) {
			return "signupPage";
		}

		List<String> roleNames = new ArrayList<String>();
		roleNames.add(AppRole.ROLE_USER);

		AppUser registered = null;

		try {
			registered = appUserDAO.registerNewUserAccount(appUserForm, roleNames);

		} catch (Exception ex) {
			ex.printStackTrace();
			model.addAttribute("errorMessage", "Error " + ex.getMessage());
			return "signupPage";
		}

		if (appUserForm.getSignInProvider() != null) {

			ProviderSignInUtils providerSignInUtils //
					= new ProviderSignInUtils(connectionFactoryLocator, connectionRepository);

			// (Spring Social API):
			// If user login by social networking.
			// This method saves social networking information to the UserConnection table.

			providerSignInUtils.doPostSignUp(registered.getUserName(), request);

		}

		// After registration is complete, automatic login.
		SecurityAuto.logInUser(registered, roleNames);

		return "redirect:/userInfo";
	}
```

Dans le code ci-dessus, l'annotation `@ModelAttribute("myForm") @Validated AppUserForm appUserForm` permet de récupérer l'objet complété par l'utilisateur, retourné par le formulaire HTML.


## Procédé de séparation des micro-services


### IHM

Posséder :

- l'end-point permettant d'accéder à la page HTML (`/signup` en *GET*) dans lequel on passe en paramètre le *HOST* de l'administration à taper : `model.addAttribute("administration_signup_endpoint", "http://168.212.226.204:8080/signup");`

**:warning: Attention le *HOST* est hard-codé à titre d'exemple ici. Voir [**le document détaillant les contraintes**](/docs/mandatory.md) pour savoir comment gérer le *HOST*.**

- la page HTML avec un formulaire : `<form th:object="${myForm}" th:action="${administration_signup_endpoint}" method="POST">...</form>`

### L'administration

Posséder:

- l'end-point permettant de récupérer l'objet du formulaire (les 2 micro-services doivent donc avoir la même définition de l'objet `AppUserForm` ici) pour ajouter l'utilisateur dans la/les BDD.
