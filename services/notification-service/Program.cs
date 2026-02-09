using Microsoft.EntityFrameworkCore;
using notification_service.Config;
using notification_service.Entities;
using notification_service.Models;
using notification_service.Repositories;
using notification_service.Services;
using notification_service.Services.Interfaces;
using Npgsql;
using Scalar.AspNetCore;
using Serilog;
using Steeltoe.Configuration.ConfigServer;
using Steeltoe.Discovery.Eureka;
using Wolverine;
using Wolverine.RabbitMQ;
using NotificationEventHandler = notification_service.Handlers.NotificationEventHandler;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddOpenApi();
builder.Services.AddControllers();

builder.Services.AddJwtAuthentication(builder.Configuration);

builder.Services.AddScoped<INotificationRepository, NotificationRepository>();
builder.Services.AddSingleton<INotificationMessageBuilder, NotificationMessageBuilder>();
builder.Services.AddSingleton<INotificationStreamService, NotificationStreamService>();
builder.Services.AddScoped<INotificationService, NotificationService>();
builder.Services.AddScoped<ICursorPaginationService, CursorPaginationService>();

builder.Host.UseSerilog((context, services, loggerConfiguration) => {
    loggerConfiguration
        .ReadFrom.Configuration(context.Configuration)
        .ReadFrom.Services(services);
});


builder.AddConfigServer();
builder.Services.AddEurekaDiscoveryClient();

var dataSourceBuilder = new NpgsqlDataSourceBuilder(
    builder.Configuration["ConnectionStrings:DefaultConnection"]
);
dataSourceBuilder.EnableDynamicJson();
var dataSource = dataSourceBuilder.Build();

builder.Services.AddDbContext<NotificationDbContext>(options => options.UseNpgsql(dataSource));

builder.Host.UseWolverine(options => {
    options.Discovery.DisableConventionalDiscovery();
    options.Discovery.IncludeType<NotificationEventHandler>();

    options.UseRabbitMq(rabbit => {
            rabbit.HostName = builder.Configuration["rabbitmq:host"]!;
            rabbit.Port = int.Parse(builder.Configuration["rabbitmq:port"]!);
            rabbit.UserName = builder.Configuration["rabbitmq:username"]!;
            rabbit.Password = builder.Configuration["rabbitmq:password"]!;
        })
        .DeclareExchange("notification.events", ex => { ex.ExchangeType = ExchangeType.Topic; })
        .DeclareQueue("notification.queue", q => { q.BindExchange("notification.events", "#"); });

    options.ListenToRabbitQueue("notification.queue")
        .DefaultIncomingMessage<NotificationEvent>();
}, ExtensionDiscovery.ManualOnly);

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment()) {
    app.MapOpenApi();
    app.MapScalarApiReference();
}

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();