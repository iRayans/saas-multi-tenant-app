variable "aws_region" {
  description = "AWS region to deploy resources"
  type        = string
  default     = "us-east-1"
}

variable "app_name" {
  description = "Application name used as prefix for resource names"
  type        = string
  default     = "saas-app"
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "db_username" {
  description = "RDS master username"
  type        = string
  default     = "postgres"
}

variable "db_password" {
  description = "RDS master password"
  type        = string
  sensitive   = true
}

variable "db_name" {
  description = "Initial database name"
  type        = string
  default     = "saasdb"
}
