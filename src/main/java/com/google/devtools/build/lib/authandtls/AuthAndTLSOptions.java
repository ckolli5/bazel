// Copyright 2017 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.authandtls;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.devtools.common.options.Converter;
import com.google.devtools.common.options.Converters.CommaSeparatedOptionListConverter;
import com.google.devtools.common.options.Converters.DurationConverter;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionDocumentationCategory;
import com.google.devtools.common.options.OptionEffectTag;
import com.google.devtools.common.options.OptionMetadataTag;
import com.google.devtools.common.options.OptionsBase;
import com.google.devtools.common.options.OptionsParsingException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * Common options for authentication and TLS.
 */
public class AuthAndTLSOptions extends OptionsBase {
  @Option(
    name = "google_default_credentials",
    oldName = "auth_enabled",
    defaultValue = "false",
    documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
    effectTags = {OptionEffectTag.UNKNOWN},
    help =
        "Whether to use 'Google Application Default Credentials' for authentication."
            + " See https://cloud.google.com/docs/authentication for details. Disabled by default."
  )
  public boolean useGoogleDefaultCredentials;

  @Option(
    name = "google_auth_scopes",
    oldName = "auth_scope",
    defaultValue = "https://www.googleapis.com/auth/cloud-platform",
    converter = CommaSeparatedOptionListConverter.class,
    documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
    effectTags = {OptionEffectTag.UNKNOWN},
    help = "A comma-separated list of Google Cloud authentication scopes."
  )
  public List<String> googleAuthScopes;

  @Option(
    name = "google_credentials",
    oldName = "auth_credentials",
    defaultValue = "null",
    documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
    effectTags = {OptionEffectTag.UNKNOWN},
    help =
        "Specifies the file to get authentication credentials from. See "
            + "https://cloud.google.com/docs/authentication for details."
  )
  public String googleCredentials;

  @Option(
      name = "tls_certificate",
      defaultValue = "null",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "Specify a path to a TLS certificate that is trusted to sign server certificates.")
  public String tlsCertificate;

  @Option(
      name = "tls_client_certificate",
      defaultValue = "null",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Specify the TLS client certificate to use; you also need to provide a client key to "
              + "enable client authentication.")
  public String tlsClientCertificate;

  @Option(
      name = "tls_client_key",
      defaultValue = "null",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Specify the TLS client key to use; you also need to provide a client certificate to "
              + "enable client authentication.")
  public String tlsClientKey;

  @Option(
    name = "tls_authority_override",
    defaultValue = "null",
    metadataTags = {OptionMetadataTag.HIDDEN},
    documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
    effectTags = {OptionEffectTag.UNKNOWN},
    help =
        "TESTING ONLY! Can be used with a self-signed certificate to consider the specified "
            + "value a valid TLS authority."
  )
  public String tlsAuthorityOverride;

  @Option(
      name = "grpc_keepalive_time",
      defaultValue = "null",
      converter = DurationConverter.class,
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Configures keep-alive pings for outgoing gRPC connections. If this is set, then "
              + "Bazel sends pings after this much time of no read operations on the connection, "
              + "but only if there is at least one pending gRPC call. Times are treated as second "
              + "granularity; it is an error to set a value less than one second. By default, "
              + "keep-alive pings are disabled. You should coordinate with the service owner "
              + "before enabling this setting.")
  public Duration grpcKeepaliveTime;

  @Option(
      name = "grpc_keepalive_timeout",
      defaultValue = "20s",
      converter = DurationConverter.class,
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Configures a keep-alive timeout for outgoing gRPC connections. If keep-alive pings are "
              + "enabled with --grpc_keepalive_time, then Bazel times out a connection if it does "
              + "not receive a ping reply after this much time. Times are treated as second "
              + "granularity; it is an error to set a value less than one second. If keep-alive "
              + "pings are disabled, then this setting is ignored.")
  public Duration grpcKeepaliveTimeout;

  @Option(
      name = "experimental_credential_helper",
      defaultValue = "null",
      allowMultiple = true,
      converter = UnresolvedScopedCredentialHelperConverter.class,
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Configures Credential Helpers to use for retrieving credentials for the provided scope"
              + " (domain).\n\n"
              + "Credentials from Credential Helpers take precedence over credentials from"
              + " <code>--google_default_credentials</code>, `--google_credentials`, or"
              + " <code>.netrc</code>.\n\n"
              + "See https://github.com/bazelbuild/proposals/blob/main/designs/2022-06-07-bazel-credential-helpers.md"
              + " for details.")
  public List<UnresolvedScopedCredentialHelper> credentialHelpers;

  @Option(
      name = "experimental_credential_helper_timeout",
      defaultValue = "5s",
      converter = DurationConverter.class,
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Configures the timeout for the Credential Helper.\n\n"
              + "Credential Helpers failing to respond within this timeout will fail the"
              + " invocation.")
  public Duration credentialHelperTimeout;

  @Option(
      name = "experimental_credential_helper_cache_duration",
      defaultValue = "30m",
      converter = DurationConverter.class,
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "Configures the duration for which credentials from Credential Helpers are cached.")
  public Duration credentialHelperCacheTimeout;

  /** One of the values of the `--credential_helper` flag. */
  @AutoValue
  public abstract static class UnresolvedScopedCredentialHelper {
    /** Returns the scope of the credential helper (if any). */
    public abstract Optional<String> getScope();

    /** Returns the (unparsed) path of the credential helper. */
    public abstract String getPath();
  }

  /** A {@link Converter} for the `--credential_helper` flag. */
  public static final class UnresolvedScopedCredentialHelperConverter
      extends Converter.Contextless<UnresolvedScopedCredentialHelper> {
    public static final UnresolvedScopedCredentialHelperConverter INSTANCE =
        new UnresolvedScopedCredentialHelperConverter();

    @Override
    public String getTypeDescription() {
      return "An (unresolved) path to a credential helper for a scope.";
    }

    @Override
    public UnresolvedScopedCredentialHelper convert(String input) throws OptionsParsingException {
      Preconditions.checkNotNull(input);

      int pos = input.indexOf('=');
      if (pos >= 0) {
        String scope = input.substring(0, pos);
        if (Strings.isNullOrEmpty(scope)) {
          throw new OptionsParsingException("Scope of credential helper must not be empty");
        }
        String path = checkPath(input.substring(pos + 1));
        return new AutoValue_AuthAndTLSOptions_UnresolvedScopedCredentialHelper(
            Optional.of(scope), path);
      }

      // `input` does not specify a scope.
      return new AutoValue_AuthAndTLSOptions_UnresolvedScopedCredentialHelper(
          Optional.empty(), checkPath(input));
    }

    private String checkPath(@Nullable String input) throws OptionsParsingException {
      if (Strings.isNullOrEmpty(input)) {
        throw new OptionsParsingException("Path to credential helper must not be empty");
      }
      return input;
    }
  }
}
