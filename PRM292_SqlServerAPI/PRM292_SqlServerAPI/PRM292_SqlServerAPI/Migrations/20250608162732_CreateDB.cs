using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace PRM292_SqlServerAPI.Migrations
{
    /// <inheritdoc />
    public partial class CreateDB : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Users",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    Email = table.Column<string>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Users", x => x.Id);
                });

            // ✅ Insert dữ liệu mẫu
            migrationBuilder.InsertData(
                table: "Users",
                columns: new[] { "Name", "Email" },
                values: new object[,]
                {
                    { "Alice Nguyen", "alice@example.com" },
                    { "Bob Tran", "bob@example.com" },
                    { "Charlie Pham", "charlie@example.com" }
                });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Users");
        }
    }
}
