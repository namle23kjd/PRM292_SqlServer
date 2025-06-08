using Microsoft.EntityFrameworkCore;
using PRM292_SqlServerAPI.Model;

var builder = WebApplication.CreateBuilder(args);

// FORCE the server to listen on all network interfaces with your new ports
builder.WebHost.UseUrls("http://0.0.0.0:61993", "https://0.0.0.0:61992");

// Add services to the container.
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Add CORS for mobile development
builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(policy =>
    {
        policy.AllowAnyOrigin()
              .AllowAnyMethod()
              .AllowAnyHeader();
    });
});

builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

// Enable CORS
app.UseCors();

// Comment out HTTPS redirection for development
// app.UseHttpsRedirection();

app.UseAuthorization();
app.MapControllers();

Console.WriteLine("?? Server running on:");
Console.WriteLine("?? Network: http://192.168.1.15:61993");
Console.WriteLine("?? Local: http://localhost:61993");
Console.WriteLine("?? Swagger: http://192.168.1.15:61993/swagger");

app.Run();