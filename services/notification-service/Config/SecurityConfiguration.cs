using System.Security.Claims;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;

namespace notification_service.Config;

public static class SecurityConfiguration {
    extension(IServiceCollection services) {
        public IServiceCollection AddJwtAuthentication(IConfiguration configuration) {
            services.AddAuthentication(options => {
                    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
                    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
                })
                .AddJwtBearer(options => {
                    var keycloakUrl = configuration["Keycloak:IssuerUri"]!;

                    options.Authority = keycloakUrl;
                    options.RequireHttpsMetadata = false;
                    options.TokenValidationParameters = new TokenValidationParameters {
                        ValidateIssuer = true,
                        ValidIssuer = keycloakUrl,
                        ValidateAudience = false,
                        ValidateLifetime = true,
                        ValidateIssuerSigningKey = true,
                        ClockSkew = TimeSpan.FromMinutes(5)
                    };

                    options.Events = new JwtBearerEvents {
                        OnAuthenticationFailed = context => {
                            var logger = context.HttpContext.RequestServices.GetRequiredService<ILogger<Program>>();
                            logger.LogError(context.Exception, "JWT Authentication failed");
                            return Task.CompletedTask;
                        },
                        OnTokenValidated = context => {
                            var logger = context.HttpContext.RequestServices.GetRequiredService<ILogger<Program>>();
                            logger.LogInformation("Token validated successfully for user {User}",
                                context.Principal?.FindFirst("sub")?.Value);
                            return Task.CompletedTask;
                        }
                    };
                });

            services.AddAuthorization();

            return services;
        }

        public IServiceCollection AddCorsPolicy(IConfiguration configuration) {
            services.AddCors(options => {
                options.AddDefaultPolicy(builder => {
                    var allowedOrigins = configuration.GetSection("Cors:AllowedOrigins").Get<string[]>()
                                         ?? ["http://localhost:4200"];

                    builder.WithOrigins(allowedOrigins)
                        .AllowAnyHeader()
                        .AllowAnyMethod()
                        .AllowCredentials();
                });
            });

            return services;
        }
    }

    extension(ClaimsPrincipal user) {
        public string? GetUserIdFromClaims() {
            return user.FindFirst(ClaimTypes.NameIdentifier)?.Value
                   ?? user.FindFirst("sub")?.Value;
        }

        public string? GetUserNameFromClaims() {
            return user.FindFirst(ClaimTypes.Name)?.Value
                   ?? user.FindFirst("preferred_username")?.Value;
        }
    }
}