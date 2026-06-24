resource "aws_db_subnet_group" "main" {
  name       = "${var.app_name}-db-subnet-group"
  subnet_ids = aws_subnet.private[*].id # return all private subnets we have (only 2)

  tags = {
    Name = "${var.app_name}-db-subnet-group"
  }
}


resource "aws_db_instance" "main" {
  identifier        = "${var.app_name}-db"
  engine            = "postgres"
  engine_version    = "16.14"
  instance_class    = "db.t3.micro"
  allocated_storage = 20
  storage_type      = "gp2"

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  vpc_security_group_ids = [aws_security_group.rds.id]
  db_subnet_group_name   = aws_db_subnet_group.main.name
  publicly_accessible    = false

  multi_az            = false # false for demo
  skip_final_snapshot = true
  deletion_protection = false

  tags = {
    Name = "${var.app_name}-db"
  }
}
