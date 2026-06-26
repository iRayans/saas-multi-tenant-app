resource "aws_ecr_repository" "app" {
  name                 = var.app_name
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = var.app_name
  }
}

# After ECR resource get created - build the project and push it to ECR.
resource "null_resource" "docker_push" {
  triggers = {
    image_tag = "latest"
  }

  provisioner "local-exec" {
    command = <<-EOT
      aws ecr get-login-password --region ${var.aws_region} | docker login --username AWS --password-stdin ${aws_ecr_repository.app.repository_url}
      docker buildx build --platform linux/amd64 -t ${aws_ecr_repository.app.repository_url}:latest --push ../
    EOT
  }

  depends_on = [aws_ecr_repository.app]
}
