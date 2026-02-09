import { NotificationType } from "./notification-type.enum";

export interface NotificationDto {
  id: string;
  eventId: string;
  type: NotificationType;
  title: string;
  message: string;
  actorUserId: string;
  resourceType: string;
  resourceId: string;
  metadata: Record<string, any>;
  createdAt: string;
  readAt: string | null;
  read?: boolean;
}
