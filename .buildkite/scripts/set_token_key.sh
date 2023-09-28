#!/bin/bash

set -eo pipefail
set +x

echo "--- Prepare vault context :vault:"
VAULT_ROLE_ID_SECRET=$(vault read -field=role-id secret/ci/elastic-thumbnails4j/internal-ci-approle)
export VAULT_ROLE_ID_SECRET

VAULT_SECRET_ID_SECRET=$(vault read -field=secret-id secret/ci/elastic-thumbnails4j/internal-ci-approle)
export VAULT_SECRET_ID_SECRET

VAULT_ADDR=$(vault read -field=vault-url secret/ci/elastic-thumbnails4j/internal-ci-approle)
export VAULT_ADDR

export VAULT_TOKEN=$(vault write -field=token auth/approle/login role_id="$VAULT_ROLE_ID" secret_id="$VAULT_SECRET_ID")
export SERVER_USERNAME=$(vault read -field username secret/release/nexus)
export SERVER_PASSWORD=$(vault read -field password secret/release/nexus)

# Signing keys
vault read -field key secret/release/signing >$KEY_FILE
export KEYPASS=$( vault read -field passphrase secret/release/signing )
# Import the key into the keyring
echo $KEYPASS | gpg --batch --import $KEY_FILE
