# Security Policy

## Supported coursework release

Security fixes are applied to the current `Backend` and `Frontend` branches.

## Secrets

Never commit database passwords, private keys, payment credentials, access
tokens, or production URLs containing credentials. The current configuration
loads sensitive values from environment variables and provides only safe
examples.

Credentials committed in earlier history must be treated as compromised even
after removal from the latest source. The repository owner must:

1. rotate the MySQL password;
2. revoke and regenerate the Alipay application private key;
3. update local/hosting secret settings;
4. verify that the previous values no longer work;
5. consider a coordinated history rewrite after all collaborators have backed
   up and agreed to re-clone.

## Reporting

Do not open a public Issue containing secret values. Contact the repository
owner privately, revoke the credential first, and then record a redacted
remediation Issue.

## Application security follow-up

- Add ownership tests for listing/order/profile mutations.
- Replace password-derived JWT signing with a dedicated rotated secret.
- Hash user passwords with a password hashing function such as BCrypt.
- Add request validation and rate limiting.
- Restrict CORS to deployed origins.
- Validate uploaded file types and store uploads outside the application
  process.
