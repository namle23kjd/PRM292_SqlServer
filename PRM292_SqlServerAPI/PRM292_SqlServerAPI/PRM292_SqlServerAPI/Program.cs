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

Console.WriteLine("============================================");
Console.WriteLine("📋 How to get your IPv4 Address:");
Console.WriteLine("   1. Open Terminal/Command Prompt (cmd)");
Console.WriteLine("   2. Type: ipconfig");
Console.WriteLine("   3. Find 'IPv4 Address' line (usually 192.168.x.x)");
Console.WriteLine("   4. Replace [YOUR_IPV4_ADDRESS] above with that IP");
Console.WriteLine("   5. Also update Android BASE_URL with same IP");
Console.WriteLine("============================================");

app.Run();